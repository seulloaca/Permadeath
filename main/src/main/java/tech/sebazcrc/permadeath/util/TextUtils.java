package tech.sebazcrc.permadeath.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import tech.sebazcrc.permadeath.Main;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {
    public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static List<String> formatList(String... s) {
        return Arrays.stream(s).map(TextUtils::format).collect(Collectors.toList());
    }

    public static List<String> formatList(Collection<String> s) {
        return s.stream().map(TextUtils::format).collect(Collectors.toList());
    }

    public static String format(String s) {
        s = s.replace("#&", "#");
        if (VersionManager.isRunningPostNetherUpdate()) {
            Matcher m = HEX_COLOR_PATTERN.matcher(s);
            while (m.find()) {
                String cl = s.substring(m.start(), m.end());
                s = s.replace(cl, "" + net.md_5.bungee.api.ChatColor.of(cl));
                m = HEX_COLOR_PATTERN.matcher(s);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String formatStringArray(String[] args, int startIndex) {
        StringBuilder msg = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }
        msg.deleteCharAt(msg.length() - 1); // Elimina el último espacio de sobra

        return msg.toString();
    }

    public static String formatTime(int secs) {
        if (secs < 0) return "" + secs;

        int remainder = secs % 86400;

        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = (remainder / 60) - (hours * 60);
        int seconds = (remainder % 3600) - (minutes * 60);

        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m " + seconds + "s ";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s ";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s ";
        } else {
            return seconds + "s ";
        }
    }

    public static String formatInterval(int totalTime) {
        int hrs = totalTime / 3600;
        int minAndSec = totalTime % 3600;
        int min = minAndSec / 60;
        int sec = minAndSec % 60;

        if (hrs > 0) {
            return String.format("%02d:%02d:%02d", hrs, min, sec);
        } else {
            return String.format("%02d:%02d", min, sec);
        }
    }

    public static String formatPosition(Location l) {
        if (l == null) return "Posición nula";
        return "X: " + ((int) l.getX()) + ", Y: " + ((int) l.getY()) + ", Z: " + ((int) l.getZ());
    }

    public static void log(String s) {
        Bukkit.getConsoleSender().sendMessage(format((!s.isEmpty() ? Main.prefix : "") + s));
    }
}
