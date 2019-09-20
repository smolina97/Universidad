package application.model;

import java.io.File;

public final class EntryComponent {
    private boolean validComponent = true;

    private String name;
    private int numBitsOffset;
    private int bitCount;
    private EntryType type;
    private File associatedList;
    private AccessType accessType;
    private int entrySize;
    private int baseAddress;
    private boolean isPointer = false;
    private Object defaultValue;

    EntryComponent(int entrySize, int baseAddress) {
        this.entrySize = entrySize;
        this.baseAddress = baseAddress;
    }

    private int parseVerboseInt(String input) {
        String loadedString = String.format("%s", input);
        boolean negative = false;
        int base = 10;
        int shift = 3;
        int retVal = -1;
        if (input.charAt(0) == '-') {
            negative = true;
            input = input.substring(1);
        }
        if (input.indexOf("0x") == 0) {
            base = 16;
            input = input.substring(2);
        }
        if (input.indexOf("b") == input.length() - 1) {
            shift = 0;
            input = input.substring(0, input.length() - 1);
        }
        try {
            retVal = Integer.parseInt(input, base);
        } catch (Exception e) {
            validComponent = false;
        }
        if (validComponent) {
            retVal <<= shift;
            if (negative)
                retVal = 0 - retVal;
        }
        return retVal;
    }

    boolean isValid() { return validComponent; }

    void setName(String input) {
        if (input == null)
            validComponent = false;
        else
            name = input;
    }

    void setNumBitsOffset(String input) {
        if (input == null)
            validComponent = false;
        else
            numBitsOffset = parseVerboseInt(input);
        if (numBitsOffset < 0) {
            validComponent = false;
        }
    }

    void setBitCount(String input) {
        if (input == null)
            validComponent = false;
        else
            bitCount = parseVerboseInt(input);
        if (bitCount <= 0) {
            validComponent = false;
        }
    }

    void setType(String input) {
        String[] split = input.split(" ");
        try {
            type = EntryType.valueOf(split[0]);
        } catch (Exception e) {
            validComponent = false;
        }
        if (type == EntryType.HEXA && (numBitsOffset % 8 != 0 || bitCount % 8 != 0))
            validComponent = false;
        if (type != EntryType.HEXA && bitCount > 32)
            validComponent = false;

        if(split.length > 1)
            parseValue(split[1]);
    }

    void setAssociatedList(String input) {
        if (input == null)
            validComponent = false;
        else if (input.equals("NULL"))
            associatedList = null;
        else
            associatedList = new File(input);
    }

    private void parseValue(String val) {
        if(type == EntryType.HEXA) {
            byte[] bytes = new byte[val.length() / 2];
            for(int x = 0; x < bytes.length; x++) {
                String sub = val.substring(x * 2, x * 2 + 2);
                bytes[x] = (byte)Integer.parseInt(sub, 16);
            }
            defaultValue = bytes;
        }
        else if(type == EntryType.CLRP) {
            defaultValue = val;
        }
        else {
            if(val.startsWith("0x"))
                defaultValue = Integer.decode(val);
            else
                defaultValue = Integer.parseInt(val);
        }
    }

    public String getName() { return name; }

    public int getOffset() { return numBitsOffset / 8; }

    public int getAddress(int index) {
        return (numBitsOffset >> 3) + (entrySize * index) + baseAddress;
    }

    public int getBitCount() { return bitCount; }

    private void calculateAccessType(int index) {
        final int significand = (getAddress(index) << 3) + (numBitsOffset % 8);
        final int bits = bitCount;
        final AccessType supposedType = TargetFile.calculateAccessType(significand);
        if (supposedType != AccessType.BIT && bits == 8)
            accessType = AccessType.BYTE;
        else if ((supposedType == AccessType.HALF || supposedType == AccessType.WORD) && bits == 16)
            accessType = AccessType.HALF;
        else if (supposedType == AccessType.WORD && bits == 32)
            accessType = AccessType.WORD;
        else
            accessType = AccessType.BIT;
    }

    public AccessType getAccessType(int index) {
        if (accessType == null) calculateAccessType(index);
        return accessType;
    }

    public EntryType getEntryType() { return type; }

    public String getAssociatedListPath() {
        return associatedList.getPath();
    }

    public boolean isPointer() {
        return isPointer;
    }

    void setPointer(boolean pointer) {
        isPointer = pointer;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
