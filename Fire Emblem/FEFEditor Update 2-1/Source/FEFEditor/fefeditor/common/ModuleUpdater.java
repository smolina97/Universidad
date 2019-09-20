package fefeditor.common;

import fefeditor.data.FileData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ModuleUpdater
{

    private static final List<Path> modulePaths = Arrays.asList(
            Paths.get(FileData.getInstance().getModules() + "/Chapter/chapter.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Character/character.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Class/class.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Skill/skill.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Stat/stat.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Army/army.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Weapon Rank/WeaponRank.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Item/item.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Forge/forge.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Tutorial/tutorial.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Path Bonus/PathBonus.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Battle Bonus/BattleBonus.nmm"),
            Paths.get(FileData.getInstance().getModules() + "/Visit Bonus/VisitBonus.nmm")
    );

    public static void updateModuleOffsets(byte[] data) {
        for (Path p : modulePaths) {
            int offset = 0;
            switch (p.getFileName().toString().toLowerCase()) {
                case "chapter.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x0, 0x4)) + 0x20;
                    break;
                case "character.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x8, 0xC)) + 0x30;
                    break;
                case "class.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0xC, 0x10)) + 0x28;
                    break;
                case "skill.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x10, 0x14)) + 0x20;
                    break;
                case "stat.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x1C, 0x20)) + 0x20;
                    break;
                case "army.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x24, 0x28)) + 0x20;
                    break;
                case "weaponrank.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x3C, 0x40)) + 0x20;
                    break;
                case "item.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x2C, 0x30)) + 0x28;
                    break;
                case "forge.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x38, 0x3C)) + 0x20;
                    break;
                case "tutorial.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x10, 0x14)) + 0x20;
                    break;
                case "pathbonus.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x5C, 0x60));
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, offset, offset + 4)) + 0x20;
                    break;
                case "battlebonus.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x5C, 0x60));
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, offset + 0x8, offset + 0xC)) + 0x20;
                    break;
                case "visitbonus.nmm":
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, 0x5C, 0x60));
                    offset = (int) ArrayUtils.toInteger(Arrays.copyOfRange(data, offset + 0x10, offset + 0x14)) + 0x20;
                    break;
            }
            try {
                List<String> lines = Files.readAllLines(p);
                lines.set(4, "0x" + Long.toHexString(offset).toUpperCase());
                Files.write(p, lines);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
