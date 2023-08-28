package tech.sebazcrc.permadeath.discord;

import org.bukkit.OfflinePlayer;

public class DiscordPortal {

    public static boolean isJDAInstalled() {
        try {
            Class.forName("net.dv8tion.jda.api.JDA");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static void banPlayer(OfflinePlayer off, boolean isAFKBan) {
        if (!isJDAInstalled()) return;
        DiscordManager.getInstance().banPlayer(off, isAFKBan);
    }

    public static void onDeathTrain(String msg) {
        if (!isJDAInstalled()) return;
        DiscordManager.getInstance().onDeathTrain(msg);
    }

    public static void onDayChange() {
        if (!isJDAInstalled()) return;
        DiscordManager.getInstance().onDayChange();
    }

    public static void onDisable() {
        if (!isJDAInstalled()) return;
        DiscordManager.getInstance().onDisable();
    }

    public static void reload() {
        if (!isJDAInstalled()) return;
        DiscordManager.getInstance();
    }
}
