package tech.sebazcrc.permadeath.discord;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.manager.Data.PlayerDataManager;
import tech.sebazcrc.permadeath.util.manager.Log.PDCLog;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Objects;

public class DiscordManager {

    private static DiscordManager discordManager;
    private final Main instance;

    @Getter
    private final File file;
    @Getter
    private final FileConfiguration configuration;

    @Getter
    private JDA bot;

    public DiscordManager() {
        this.instance = Main.getInstance();

        this.file = new File(instance.getDataFolder(), "discord.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        if (!file.exists()) {
            this.instance.saveResource("discord.yml", false);
        }

        if (configuration.getBoolean("Enable")) {
            log("Intentando cargar la aplicación de Discord.");

            String token = configuration.getString("Token");

            if (token.isEmpty()) {
                log("No se ha proporcionado un token por el usuario");
                return;
            }

            try {
                JDABuilder builder = JDABuilder.createDefault(token);
                builder.setActivity(Activity.watching(Objects.requireNonNull(configuration.getString("Status"))));

                this.bot = builder.build();
                this.bot.awaitReady();
            } catch (InterruptedException e) {
                log("Ha ocurrido un error al iniciar sesión con la aplicación de Discord, revisa tu token.");
                e.printStackTrace();
            }

            try {
                String s = configuration.getString("Channels.Anuncios");
                if (s == null) return;
                TextChannel channel = bot.getTextChannelById(s);
                if (channel == null) return;
                sendEmbed(channel, buildEmbed("Permadeath", Color.GREEN, null, null, null, ":gear: Plugin encendido."));
            } catch (Exception ignored) {
            }
        } else {
            log("El bot de discord no está activado en la config");
        }
    }

    public static DiscordManager getInstance() {
        if (discordManager == null) discordManager = new DiscordManager();

        return discordManager;
    }

    public void onDisable() {
        if (this.bot == null) return;
        String s = configuration.getString("Channels.Anuncios");
        if (s == null) return;
        TextChannel channel = bot.getTextChannelById(s);

        if (channel == null) return;
        sendEmbed(channel, buildEmbed("Permadeath", Color.RED, null, null, null, ":gear: Plugin desactivado."));
    }

    public void onDeathTrain(String msg) {
        if (this.bot == null) return;
        String s = configuration.getString("Channels.Anuncios");
        if (s == null) return;
        TextChannel channel = bot.getTextChannelById(s);

        if (channel == null) return;
        sendEmbed(channel, buildEmbed("Permadeath", Color.RED, null, null, null, ":fire: " + ChatColor.stripColor(msg)));
    }

    public void onDayChange() {
        if (this.bot == null) return;
        String s = configuration.getString("Channels.Anuncios");
        if (s == null) return;
        TextChannel channel = bot.getTextChannelById(s);

        if (channel == null) return;
        sendEmbed(channel, buildEmbed("Permadeath", Color.GREEN, null, null, null, ":alarm_clock: Han avanzado al día " + instance.getDay()));
    }

    public void banPlayer(OfflinePlayer off, boolean isAFKBan) {
        if (this.bot == null) return;

        Player p = (off.isOnline() ? (Player) off : null);

        PlayerDataManager data = new PlayerDataManager(off.getName(), instance);
        String playerLoc = (isAFKBan ? "" : p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());

        String serverName = configuration.getString("ServerName");
        LocalDate n = LocalDate.now();
        String date = String.format("%02d/%02d/%02d", n.getDayOfMonth(), n.getMonthValue(), n.getYear());
        String cause = isAFKBan ? "AFK" : data.getBanCause();

        EmbedBuilder b = buildEmbed(off.getName() + " ha sido PERMABANEADO en " + serverName + "\n",
                new Color(0xF40C0C),
                null,
                null,
                "https://mineskin.eu/headhelm/" + off.getName() + "/100.png");
        b.setAuthor("Permadeath", "https://twitter.com/SebazCRC", "https://www.spigotmc.org/data/avatars/l/429/429856.jpg?1692799382");
        b.addField("\uD83D\uDCC5 Fecha", date, true);
        b.addField("\uD83D\uDC80 Razón", cause, true);
        if (!isAFKBan) b.addField("\uD83E\uDDED Coordenadas", playerLoc, true);

        TextChannel channel = getBot().getTextChannelById(configuration.getString("Channels.DeathChannel"));

        if (channel == null) log("No pudimos encontrar el canal de muertes.");

        assert channel != null;
        channel.sendMessageEmbeds(b.build()).queue(message -> {
            message.addReaction(Emoji.fromFormatted("☠")).queue();
        });

        log("Enviando mensaje de muerte a discord");
    }

    private void log(String s) {
        PDCLog.getInstance().log("[DISCORD] " + s);
    }

    private EmbedBuilder buildEmbed(String title, Color color, String footer, String image, String thumbnail, String... description) {
        EmbedBuilder eb = new EmbedBuilder();

        if (title != null) eb.setTitle(title);
        if (color != null) eb.setColor(color);
        if (footer != null) eb.setFooter(footer);
        if (image != null) eb.setImage(image);
        if (thumbnail != null) eb.setThumbnail(thumbnail);

        for (String s : description) {
            eb.addField("", s, false);
        }

        return eb;
    }

    private void sendEmbed(MessageChannel channel, EmbedBuilder b, String... reaction) {
        channel.sendMessageEmbeds(b.build()).queue(message -> {
            for (String s : reaction) {
                message.addReaction(Emoji.fromFormatted(s)).queue();
            }
        });
    }
}
