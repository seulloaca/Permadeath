package tech.sebazcrc.permadeath.util.lib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private Plugin plugin;
    private int resourceId;
    private boolean hasInternetConection = true;

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        this.resourceId = 78993;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getServer().getConsoleSender().sendMessage(TextUtils.format(Main.prefix + "&7> &4&lNO SE HA PODIDO VERIFICAR UNA ACTUALIZACIÓN"));
                this.hasInternetConection = false;
            }
        });
    }

    public boolean isHasInternetConection() {
        return hasInternetConection;
    }
}