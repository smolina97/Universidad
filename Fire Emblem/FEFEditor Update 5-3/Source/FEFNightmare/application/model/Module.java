/*
 *  Nightmare 2.0 - General purpose file editor
 *
 *  Copyright (C) 2009 Hextator,
 *  hectorofchad (AIM) hectatorofchad@sbcglobal.net (MSN)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3
 *  as published by the Free Software Foundation
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  <Description> Wrapper class for text files following both the old format of
 *  Nightmare modules as well as the new format unique to this remix of the
 *  application which was designed to enhance the usefulness of it.
 */

package application.model;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {
    private boolean valid = true;
    private int line = 0;

    private String version;
    private String description;
    private String category;
    private int baseAddress;
    private int defaultEntryCount;
    private int entrySize;
    private File entryList;
    private File textTable;
    private File moduleFile;
    private int countAddress;
    private int tableHeaderAddress;
    private boolean isInjector;
    private boolean isPointerTwo;
    private int countLength;

    private List<EntryComponent> componentList = new ArrayList<>();

    public Module(File input, boolean process) {
        moduleFile = input;
        if (input == null)
            throw new RuntimeException("invalid path");
        List<String> lines;
        try {
            lines = Files.readAllLines(input.toPath());

            // Purge empty lines and comments.
            lines.removeAll(Arrays.asList(null, ""));
            lines.removeIf(str -> str.startsWith("#"));
        } catch (Exception e) {
            valid = false;
            return;
        }
        try {
            if (process) {
                processHeader(lines);
                processValidComponents(lines);
            } else {
                category = lines.get(6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!valid) throw new RuntimeException();
    }

    private int getInt(String originalString) {
        int retVal = -1;
        String loadedString;
        boolean negative = false;
        try {
            loadedString = originalString;
            if (loadedString.charAt(0) == '-') {
                loadedString = loadedString
                        .substring(1);
                negative = true;
            }
            int base = 10;
            if (loadedString.indexOf("0x") == 0) {
                base = 16;
                loadedString = loadedString.substring(2);
            }
            retVal = Integer.parseInt(loadedString, base);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (valid && negative)
            retVal = 0 - retVal;
        return retVal;
    }

    private void processHeader(List<String> lines) {
        String loadedString = lines.get(line);
        version = loadedString;
        isInjector = version.contains("INJECTOR");
        line++;
        loadedString = lines.get(line);
        description = loadedString;
        line++;
        if (version.contains("RELATIVE") || version.contains("INJECTOR")) {
            String[] values = lines.get(line).split(" ");
            baseAddress = TargetFile.getInt(getInt(values[0])) + getInt(values[1]);
            tableHeaderAddress = TargetFile.getInt(getInt(values[0]));
        } else {
            baseAddress = getInt(lines.get(line));
            if (baseAddress < 0 || baseAddress > TargetFile.size()) {
                valid = false;
                throw new IllegalArgumentException("invalid base address");
            }
        }
        line++;
        if (version.contains("RELATIVE") || version.contains("INJECTOR")) {
            String[] values = lines.get(line).split(" ");
            switch (values[0]) {
                case "TABLE_OFFSET":
                    countAddress = baseAddress;
                    break;
                case "TABLE_HEADER_OFFSET":
                    countAddress = tableHeaderAddress;
                    break;
                default:
                    countAddress = getInt(values[0]);
                    break;
            }
            countAddress += getInt(values[1]);
            if (values[2].contains("WORD")) {
                defaultEntryCount = TargetFile.getInt(countAddress);
                countLength = 4;
            } else if (values[2].contains("HALF")) {
                defaultEntryCount = TargetFile.getShort(countAddress);
                countLength = 2;
            } else if (values[2].contains("NULL")) {
                defaultEntryCount = 0;
                countLength = -1;
            } else {
                defaultEntryCount = TargetFile.getByte(countAddress);
                countLength = 1;
            }
        } else {
            defaultEntryCount = getInt(lines.get(line));
        }
        line++;
        if (defaultEntryCount < 0) {
            valid = false;
            throw new IllegalArgumentException("invalid default entry count");
        }
        entrySize = getInt(lines.get(line));
        line++;
        if (entrySize <= 0) {
            valid = false;
            throw new IllegalArgumentException("invalid entry size: " + entrySize);
        }
        loadedString = lines.get(line);
        line++;
        if (loadedString.equals("NULL"))
            textTable = null;
        else
            entryList = new File(moduleFile.getParent(), loadedString);
        category = lines.get(line);
        line++;
        if (isInjector) {
            isPointerTwo = Boolean.parseBoolean(lines.get(line));
            line++;
        }
    }

    private void processValidComponents(List<String> lines) {
        while (true) {
            EntryComponent currentComponent = new EntryComponent(entrySize, baseAddress);
            currentComponent.setName(lines.get(line));
            line++;
            if (!currentComponent.isValid()) {
                line += 4;
                continue;
            }
            if (lines.get(line).endsWith("*")) {
                currentComponent.setPointer(true);
            }
            currentComponent.setNumBitsOffset(lines.get(line).replace("*", ""));
            line++;
            if (!currentComponent.isValid()) {
                line += 3;
                continue;
            }
            currentComponent.setBitCount(lines.get(line));
            line++;
            if (!currentComponent.isValid()) {
                line += 2;
                continue;
            }
            currentComponent.setType(lines.get(line));
            line++;
            if (!currentComponent.isValid()) {
                line++;
                continue;
            }
            if (lines.get(line).equals("NULL"))
                currentComponent.setAssociatedList(lines.get(line));
            else
                currentComponent.setAssociatedList(moduleFile.getParent() + File.separator + lines.get(line));
            line++;

            if (currentComponent.isValid())
                componentList.add(currentComponent);

            if (line >= lines.size())
                break;
        }
    }

    @Override
    public String toString() {
        String outputString = "\n";
        outputString += "Path: " + moduleFile.getPath() + "\n";
        outputString += "Version: " + version + "\n";
        outputString += "Description: " + description + "\n";
        outputString += "Base address: " + String.format("0x%08X", baseAddress) + "\n";
        outputString += "Default entry count: " + String.format("0x%X", defaultEntryCount) + "\n";
        outputString += "Entry size: " + String.format("0x%X", entrySize) + "\n";
        if (entryList != null)
            outputString += "Entry list path: " + entryList.getPath() + "\n";
        else
            outputString += "No entry list\n";
        if (textTable != null)
            outputString += "Text table path: " + textTable.getPath();
        else
            outputString += "No text table\n";
        return outputString;
    }

    public final File getEntryList() {
        return entryList;
    }

    public final List<EntryComponent> getComponentList() {
        return componentList;
    }

    public int getDefaultEntryCount() {
        return defaultEntryCount;
    }

    public int getCountAddress() {
        return countAddress;
    }

    public boolean isInjector() {
        return isInjector;
    }

    public int getBaseAddress() {
        return baseAddress;
    }

    public int getEntrySize() {
        return entrySize;
    }

    public int getCountLength() {
        return countLength;
    }

    public File getModuleFile() {
        return moduleFile;
    }

    public boolean isPointerTwo() {
        return isPointerTwo;
    }

    public String getCategory() {
        return category;
    }
}
