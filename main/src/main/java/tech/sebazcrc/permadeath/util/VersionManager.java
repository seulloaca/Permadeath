package tech.sebazcrc.permadeath.util;

import lombok.Getter;
import org.bukkit.Bukkit;

public class VersionManager {
    @Getter
    private static final String version;
    @Getter
    private static MinecraftVersion minecraftVersion;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].substring(1);
        try {
            minecraftVersion = MinecraftVersion.valueOf("v" + getRev());
        } catch (Exception ignored) {
        }
    }

    public static String getRev() {
        return getVersion();
    }

    public static boolean isValidVersionSet() {
        return minecraftVersion != null;
    }

    public static String getFormattedVersion() {
        return minecraftVersion.getFormattedName();
    }

    public static boolean isRunningPostNetherUpdate() {
        return minecraftVersion != MinecraftVersion.v1_15_R1;
    }
}
