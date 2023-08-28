package tech.sebazcrc.permadeath;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.data.Language;
import tech.sebazcrc.permadeath.util.NMS;
import tech.sebazcrc.permadeath.util.item.InfernalNetherite;
import tech.sebazcrc.permadeath.util.item.NetheriteArmor;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.util.manager.Data.DateManager;
import tech.sebazcrc.permadeath.util.manager.Data.PlayerDataManager;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.discord.DiscordPortal;
import tech.sebazcrc.permadeath.util.VersionManager;

import java.time.LocalTime;
import java.util.Random;
import java.util.stream.Collectors;

public class PDCCommand implements CommandExecutor {

    public static int dias;
    private Main instance;

    public PDCCommand(Main instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Comando por argumentos
        if (command.getName().equalsIgnoreCase("pdc")) {

            World world = instance.world;
            World endWorld = instance.endWorld;

            CommandSender player = sender;

            if (args.length == 0) {

                sendHelp(player);
                return false;
            } else {

                if (args[0].equalsIgnoreCase("awake")) {

                    if (player instanceof Player) {

                        int timeAwake = ((Player) player).getStatistic(Statistic.TIME_SINCE_REST) / 20;

                        long hours = timeAwake % 86400 / 3600;
                        long minutes = (timeAwake % 3600) / 60;
                        long seconds = timeAwake % 60;
                        long days = timeAwake / 86400;

                        String awake = (days >= 1 ? days + " días " : "") + String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        player.sendMessage(instance.prefix + ChatColor.RED + "Tiempo despierto: " + ChatColor.GRAY + awake);
                    }

                } else if (args[0].equalsIgnoreCase("duracion")) {
                    boolean weather = world.hasStorm();
                    int stormDurationTick = world.getWeatherDuration();
                    int stormTicksToSeconds = stormDurationTick / 20;

                    int seconds = stormTicksToSeconds;

                    if (weather) {
                        if (seconds < 86400) {

                            LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                            String time = timeOfDay.toString();

                            player.sendMessage(instance.prefix + ChatColor.RED + "Quedan " + ChatColor.GRAY + time);

                        } else {
                            dias = 0;
                            while (seconds > 86400) {
                                seconds -= 86400;
                                dias += 1;
                            }

                            LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                            String time = timeOfDay.toString();

                            player.sendMessage(instance.prefix + ChatColor.RED + "Quedan " + ChatColor.GRAY + dias + "d " + time);
                        }
                    } else {
                        player.sendMessage(instance.prefix + ChatColor.RED + "¡No hay ninguna tormenta en marcha!");
                    }

                } else if (args[0].equalsIgnoreCase("idioma")) {

                    if (!(player instanceof Player)) return false;

                    Player p = (Player) player;

                    if (args.length == 1) {

                        sender.sendMessage(TextUtils.format("&ePor favor ingresa un idioma."));
                        p.sendMessage(TextUtils.format("&7Ejemplo: &b/pdc idioma es"));
                        p.sendMessage(TextUtils.format("&eArgumentos válidos: &b<es, en>"));
                        return false;
                    }

                    String lang = args[1];
                    PlayerDataManager data = new PlayerDataManager(p.getName(), instance);

                    if (lang.equalsIgnoreCase("es")) {
                        if (data.getLanguage() == Language.SPANISH) {

                            p.sendMessage(TextUtils.format("&c¡Ya estás usando el idioma español!"));
                            return false;
                        }

                        data.setLanguage(Language.SPANISH);
                        p.sendMessage(TextUtils.format("&eHas cambiado tu idioma a: &bEspañol"));

                    } else if (lang.equalsIgnoreCase("en")) {
                        if (data.getLanguage() == Language.ENGLISH) {

                            p.sendMessage(TextUtils.format("&cYour language is already set to english"));
                            return false;
                        }

                        data.setLanguage(Language.ENGLISH);
                        p.sendMessage(TextUtils.format("&eYour language has been set to: &bEnglish"));

                    } else {
                        p.sendMessage(TextUtils.format("&cNo has ingresado un idioma válido."));
                    }

                } else if (args[0].equalsIgnoreCase("cambiarDia")) {

                    if (!player.hasPermission("permadeathcore.cambiardia")) {
                        player.sendMessage(TextUtils.format("&cNo tienes permiso para hacer esto"));
                        return false;
                    }

                    if (Main.SPEED_RUN_MODE) {
                        player.sendMessage(TextUtils.format("&cNo puedes hacer esto por que el modo SpeedRun está activo."));
                        return false;
                    }

                    if (args.length <= 1) {

                        player.sendMessage(TextUtils.format("&cNecesitas agregar un día"));
                        player.sendMessage(TextUtils.format("&eEjemplo: &7/pdc cambiarDia <día>"));
                        return false;
                    }

                    DateManager.getInstance().setDay(player, args[1]);

                } else if (args[0].equalsIgnoreCase("reload")) {

                    if (player.hasPermission("permadeathcore.reload")) {

                        instance.reload(player);

                    } else {

                        player.sendMessage(TextUtils.format("&cNo tienes permiso para utilizar este comando."));
                    }
                } else if (args[0].equalsIgnoreCase("debug")) {

                    if (!(sender instanceof Player)) {
                        player.sendMessage(TextUtils.format("&cNecesitas usar este comando en el juego."));
                        return false;
                    }

                    Player p = (Player) sender;

                    if (!p.hasPermission("permadeathcore.admin")) {
                        player.sendMessage(TextUtils.format("&cNo tienes permisos para utilizar este comando (&f&npermadeathcore.admin&c)."));
                        return false;
                    }

                    if (args.length == 1) {
                        player.sendMessage(TextUtils.format(instance.prefix + "&eEste comando te servirá en nuestro soporte si tienes problemas"));
                        player.sendMessage(TextUtils.format("&b&nSub comandos:"));
                        player.sendMessage(TextUtils.format("&7/pdc debug info&f&l- &eInformación importante acerca del plugin, suele usarse en soporte."));
                        player.sendMessage(TextUtils.format("&7/pdc debug generate_beginning&f&l- &eSi tienes problemas con The Beginning puedes generarlo manualmente."));
                        //player.sendMessage(instance.format("&7/pdc debug &f&l- &e."));
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("info")) {

                        player.sendMessage(TextUtils.format(instance.prefix + "&6&lMostrando información debug para soporte"));
                        player.sendMessage(TextUtils.format(" "));
                        player.sendMessage(TextUtils.format("&fDía actual: &a" + DateManager.getInstance().getDay()));
                        player.sendMessage(TextUtils.format("&fWorldEdit: " + (Bukkit.getPluginManager().getPlugin("WorldEdit") == null ? "&cNo instalado" : "&aInstalado, &eversión: &b" + Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion())));
                        player.sendMessage(TextUtils.format("&fVersión del Plugin: &a" + this.instance.getDescription().getVersion()));
                        player.sendMessage(TextUtils.format("&fVersión del Servidor: &a" + VersionManager.getFormattedVersion() + " &b(" + VersionManager.getVersion() + ")"));
                        player.sendMessage(TextUtils.format("&fMundo de overworld: &a" + instance.world.getName()));
                        player.sendMessage(TextUtils.format("&fMundo de end: &a" + instance.endWorld.getName()));
                        player.sendMessage(TextUtils.format(""));
                        player.sendMessage(TextUtils.format("&eEsta información es brindada en nuestro discord, &f&nhttps://discord.gg/w58wzrcJU8"));

                        //player.sendMessage(instance.format("&"));

                    } else if (args[1].equalsIgnoreCase("generate_beginning")) {


                    } else if (args[1].equalsIgnoreCase("toggle")) {
                        Main.DEBUG = !Main.DEBUG;
                        player.sendMessage("Debug cambiado a " + Main.DEBUG);

                    } else if (args[1].equalsIgnoreCase("module")) {
                        NMS.spawnDeathModule(p.getLocation());
                    } else if (args[1].equalsIgnoreCase("health")) {
                        player.sendMessage("Vida máxima: " + NetheriteArmor.getAvailableMaxHealth(p));
                    } else if (args[1].equalsIgnoreCase("events")) {
                        player.sendMessage("Eventos:");
                        player.sendMessage("Shulker shell: " + (Main.getInstance().getShulkerEvent().isRunning() ? "corriendo, tiempo: " + Main.getInstance().getShulkerEvent().getTimeLeft() + ", bossbar:" + Main.getInstance().getShulkerEvent().getBossBar().getTitle() : "no corriendo"));
                        player.sendMessage("Life Orb: " + (Main.getInstance().getOrbEvent().isRunning() ? "corriendo, tiempo: " + Main.getInstance().getOrbEvent().getTimeLeft() + ", bossbar:" + Main.getInstance().getOrbEvent().getBossBar().getTitle() : "no corriendo"));
                    } else if (args[1].equalsIgnoreCase("hasOrb")) {
                        boolean hasOrb = (instance.getOrbEvent().isRunning() ? true : NetheriteArmor.checkForOrb(p));
                        p.sendMessage("Orb: " + hasOrb);
                    } else if (args[1].equalsIgnoreCase("hyper")) {
                        boolean doPlayerAteOne = p.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "hyper_one"), PersistentDataType.BYTE);
                        boolean doPlayerAteTwo = p.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "hyper_two"), PersistentDataType.BYTE);
                        p.sendMessage("Gap 1: " + doPlayerAteOne + " | Gap2:" + doPlayerAteTwo);
                    } else if (args[1].equalsIgnoreCase("removegaps")) {

                        p.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "hyper_one"));
                        p.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "hyper_two"));
                    } else if (args[1].equalsIgnoreCase("showHealthSkeleton")) {

                        int d = Integer.parseInt(args[2]);

                        p.sendMessage("Actual health: " + (d < 50 ? 25 : d < 60 ? 40 : 110)); // Día 30);
                    } else if (args[1].equalsIgnoreCase("withertime")) {

                        p.sendMessage("tiempo: " + p.getPersistentDataContainer().get(new NamespacedKey(instance, "wither"), PersistentDataType.INTEGER));

                    } else if (args[1].equalsIgnoreCase("testtotems")) {
                        p.sendMessage("Totems sin offhand debug: " + p.getInventory().all(Material.TOTEM_OF_UNDYING).size());
                    } else if (args[1].equalsIgnoreCase("testtotemsb")) {

                        int totems = p.getInventory().all(Material.TOTEM_OF_UNDYING).size();

                        if (p.getInventory().getItemInOffHand() != null) {
                            if (p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) totems++;
                        }

                        p.sendMessage("Totems con offhand debug: " + totems);
                    } else if (args[1].equalsIgnoreCase("testlingering")) {
                        p.sendMessage("Lingering: " + Main.DISABLED_LINGERING);
                    } else if (args[1].equalsIgnoreCase("summonske")) {
                        WitherSkeleton skeleton = p.getWorld().spawn(p.getLocation().clone(), WitherSkeleton.class);
                        skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 32765).build());
                        skeleton.getEquipment().setItemInMainHandDropChance(0.0f);

                        skeleton.setRemoveWhenFarAway(false);
                        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                        skeleton.setCustomName("&6Ultra Esqueleto Definitivo");
                        instance.getNmsAccessor().setMaxHealth(skeleton, 400.0D, true);
                    } else if (args[1].equalsIgnoreCase("testwither")) {
                        p.getPersistentDataContainer().set(new NamespacedKey(instance, "wither"), PersistentDataType.INTEGER, 3595);
                    } else if (args[1].equalsIgnoreCase("spawncreeper")) {
                        Location l = p.getLocation().clone();
                        int y = (int) l.getY();
                        while (y < l.getWorld().getMaxHeight() - 1 && l.getWorld().getBlockAt((int) l.getX(), y, (int) l.getZ()).getType() != Material.AIR) {
                            y++;
                        }

                        Random r = new Random();
                        int pX = (r.nextBoolean() ? -1 : 1) * r.nextInt(19);
                        int pZ = (r.nextBoolean() ? -1 : 1) * r.nextInt(19);
                        if (y == l.getWorld().getMaxHeight() - 1) y = l.getWorld().getHighestBlockYAt(pX, pZ);
                        Location f = new Location(l.getWorld(), l.getX() + pX, y, l.getZ() + pZ);

                        if (f.getBlock().getType() == Material.AIR && f.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                            instance.getFactory().spawnEnderQuantumCreeper(f, null);
                            p.sendMessage("x: " + f.getBlockX() + " y: " + f.getBlockY() + " z: " + f.getBlockZ());
                        }
                    } else if (args[1].equalsIgnoreCase("addtimespeedrun")) {
                        instance.setPlayTime(instance.getPlayTime() + Integer.valueOf(args[2]));
                        //} else if (args[1].equalsIgnoreCase("")) {
                    } else if (args[1].equalsIgnoreCase("muerte")) {
                        boolean b = Boolean.parseBoolean(args[3]);
                        DiscordPortal.banPlayer(Bukkit.getOfflinePlayer(args[2]), b);
                    } else {
                        player.sendMessage(TextUtils.format("&c¡No existe ese sub-comando!"));
                    }
                } else if (args[0].equalsIgnoreCase("mensaje")) {

                    if (!(player instanceof Player)) return false;

                    if (args.length == 1) {

                        player.sendMessage(TextUtils.format("&cDebes escribir un mensaje, ejemplo: /ic mensaje He muerto"));

                        if (instance.getConfig().contains("Server-Messages.CustomDeathMessages." + player.getName())) {

                            player.sendMessage(TextUtils.format("&eTu mensaje de muerte actual es: &7" + instance.getConfig().getString("Server-Messages.CustomDeathMessages." + player.getName())));
                        } else {

                            player.sendMessage(TextUtils.format("&eTu mensaje de muerte actual es: &7" + instance.getConfig().getString("Server-Messages.DefaultDeathMessage")));
                        }

                        return false;
                    }

                    String msg = "";

                    for (int i = 0; i < args.length; i++) {
                        if (!args[i].equalsIgnoreCase(args[0])) {

                            String s = args[i];
                            msg = msg + " " + s;
                        }
                    }

                    if (msg.contains("&")) {

                        player.sendMessage(ChatColor.RED + "No se admite el uso de " + ChatColor.GOLD + "&");
                        return false;
                    }

                    instance.getConfig().set("Server-Messages.CustomDeathMessages." + player.getName(), "&7" + msg);
                    instance.saveConfig();
                    instance.reloadConfig();

                    if (player instanceof Player) {
                        ((Player) player).playSound(((Player) player).getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, -5);
                    }
                    player.sendMessage(TextUtils.format("&eHas cambiado tu mensaje de muerte a: &7" + msg));

                } else if (args[0].equalsIgnoreCase("dias")) {
                    if (instance.getDay() < 1) {
                        player.sendMessage(instance.prefix + ChatColor.DARK_RED + "[ERROR] Se ha producido un error al cargar el dia, config.yml mal configurado.");
                    } else {
                        if (Main.SPEED_RUN_MODE) {
                            player.sendMessage(instance.prefix + ChatColor.RED + "Estamos en la hora: " + ChatColor.GRAY + instance.getDay());
                        } else {
                            player.sendMessage(instance.prefix + ChatColor.RED + "Estamos en el día: " + ChatColor.GRAY + instance.getDay());
                        }
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    player.sendMessage(instance.prefix + ChatColor.RED + "Version Info:");
                    player.sendMessage(ChatColor.GRAY + "- Nombre: " + ChatColor.GREEN + "PermaDeathCore.jar");
                    player.sendMessage(ChatColor.GRAY + "- Versión: " + ChatColor.GREEN + "PermaDeathCore v" + instance.getDescription().getVersion());
                    player.sendMessage(ChatColor.GRAY + "- Dificultades: " + ChatColor.GREEN + "Soportado de día 1 a día 60");
                    player.sendMessage(ChatColor.GRAY + "- Autor: " + ChatColor.GREEN + "Equipo de InfernalCore (Desarrollador principal: SebazCRC)");
                } else if (args[0].equalsIgnoreCase("discord")) {
                    player.sendMessage(instance.prefix + ChatColor.BLUE + "https://discord.gg/w58wzrcJU8 | https://discord.gg/infernalcore");

                } else if (args[0].equalsIgnoreCase("cambios")) {

                    player.sendMessage(TextUtils.format("&eEste plugin contiene &c&lTODOS &r&elos cambios de PermaDeath."));
                    player.sendMessage(TextUtils.format("&eMás información aquí:"));
                    player.sendMessage(TextUtils.format("&b> &f&lhttps://twitter.com/permadeathsmp"));
                    player.sendMessage(TextUtils.format("&b> &f&lhttps://permadeath.fandom.com/es/wiki/Cambios_de_dificultad"));

                } else if (args[0].equalsIgnoreCase("beginning")) {

                    if (!player.hasPermission("permadeathcore.admin")) {
                        player.sendMessage(TextUtils.format("&cNo tienes permiso para ejecutar este comando."));
                        return false;
                    }

                    if (args.length == 1) {
                        player.sendMessage(TextUtils.format(instance.prefix + "&cLista de comandos para The Beginning"));
                        player.sendMessage(TextUtils.format("&7/pdc beginning bendicion <jugador> &f&l- &cOtorga la bendición de The Beginning a un jugador."));
                        player.sendMessage(TextUtils.format("&7/pdc beginning maldicion <jugador> &f&l- &cOtorga la maldición de The Beginning a un jugador."));
                        //player.sendMessage(instance.format("&7/pdc speedrun &f&l- &c."));
                        return false;
                    }

                    if (args.length == 2) {
                        player.sendMessage(TextUtils.format("&cEscribe el nombre de un jugador."));
                        return false;
                    }

                    Player off = Bukkit.getPlayer(args[2]);
                    if (off == null) {
                        player.sendMessage(TextUtils.format("&c¡No hemos podido encontrar a ese jugador!"));
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("bendicion")) {
                        player.sendMessage(TextUtils.format("&aSe ha otorgado la bendición de The Beginning a &b" + off.getName()));
                        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&lEnhorabuena " + off.getName() + " has recibido la bendición del comienzo por entrar primero a The Beginning. Suerte."));
                        off.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (60 * 60 * 12 * 20), 1));
                    }

                    if (args[1].equalsIgnoreCase("maldicion")) {
                        player.sendMessage(TextUtils.format("&aSe ha otorgado la maldición de The Beginning a &b" + off.getName()));
                        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&l" + off.getName() + ", ¡Desgracia! has recibido la maldición de The Beginning por entrar de último."));
                        Bukkit.broadcastMessage(TextUtils.format("&d&l¡Sufre y muere por lento! NO puedes usar cubos de leche dentro de Permadeath por 12 horas o serás PERMABANEADO."));
                        off.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (60 * 60 * 12 * 20), 0));
                        off.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (60 * 60 * 12 * 20), 0));
                    }

                } else if (args[0].equalsIgnoreCase("speedrun")) {

                    if (!player.hasPermission("permadeathcore.admin")) {
                        player.sendMessage(TextUtils.format("&cNo tienes permiso para ejecutar este comando."));
                        return false;
                    }

                    if (args.length == 1) {
                        player.sendMessage(TextUtils.format(instance.prefix + "&cLista de comandos para el modo SpeedRun"));
                        player.sendMessage(TextUtils.format("&7/pdc speedrun toggle &f&l- &cActiva o desactiva el modo SpeedRun."));
                        player.sendMessage(TextUtils.format("&7/pdc speedrun tiempo &f&l- &cObtén el tiempo de juego total."));
                        player.sendMessage(TextUtils.format("&7/pdc speedrun reset &f&l- &cReinicia el tiempo."));
                        //player.sendMessage(instance.format("&7/pdc speedrun &f&l- &c."));
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("toggle")) {
                        player.sendMessage(TextUtils.format(Main.prefix + (instance.SPEED_RUN_MODE ? "&cHas desactivado el modo SpeedRun" : "&aHas activado el modo SpeedRun")));
                        Main.SPEED_RUN_MODE = !Main.SPEED_RUN_MODE;
                    } else if (args[1].equalsIgnoreCase("tiempo")) {
                        player.sendMessage(TextUtils.format(Main.prefix + "&eEl tiempo de juego actual es de: &b" + TextUtils.formatInterval(instance.getPlayTime())));
                    } else if (args[1].equalsIgnoreCase("reset")) {
                        player.sendMessage(TextUtils.format(Main.prefix + "&aHas reiniciado el tiempo del modo SpeedRun."));
                        instance.setPlayTime(0);
                    } else {
                        player.sendMessage(TextUtils.format(instance.prefix + "&cLista de comandos para el modo SpeedRun"));
                        player.sendMessage(TextUtils.format("&7/pdc speedrun toggle &f&l- &cActiva o desactiva el modo SpeedRun."));
                        player.sendMessage(TextUtils.format("&7/pdc speedrun tiempo &f&l- &cObtén el tiempo de juego total."));
                        player.sendMessage(TextUtils.format("&7/pdc speedrun reset &f&l- &cReinicia el tiempo."));
                    }

                } else if (args[0].equalsIgnoreCase("event")) {

                    if (!player.hasPermission("permadeathcore.event")) {

                        player.sendMessage(TextUtils.format("&cNo tienes permiso para ejecutar este comando."));
                        return false;
                    }

                    if (args.length == 1) {

                        player.sendMessage(TextUtils.format("&cPor favor introduce un evento, ejemplo: &e/pdc event shulkershell"));
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("shulkershell")) {

                        if (instance.getShulkerEvent().isRunning()) {

                            player.sendMessage(TextUtils.format("&cEse evento ya está en ejecución."));
                            return false;
                        }

                        instance.getShulkerEvent().setRunning(true);
                        player.sendMessage(TextUtils.format("&aSe ha iniciado el evento correctamente."));

                        for (Player p : Bukkit.getOnlinePlayers()) {

                            instance.getShulkerEvent().addPlayer(p);
                        }
                    } else if (args[1].equalsIgnoreCase("lifeorb")) {

                        if (instance.getOrbEvent().isRunning()) {
                            player.sendMessage(TextUtils.format("&cEse evento ya está en ejecución."));
                            return false;
                        }

                        if (instance.getDay() < 60) {
                            player.sendMessage(TextUtils.format("&cEste evento solo puede ser iniciado en días superiores a 60."));
                            return false;
                        }

                        instance.getOrbEvent().setRunning(true);
                        player.sendMessage(TextUtils.format("&aSe ha iniciado el evento correctamente."));
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            instance.getOrbEvent().addPlayer(p);
                        }
                    } else {

                        player.sendMessage(TextUtils.format("&cNo hemos podido encontrar ese evento."));
                    }

                } else if (args[0].equalsIgnoreCase("locate")) {

                    if (!player.hasPermission("permadeathcore.locate")) {

                        player.sendMessage(TextUtils.format("&cNo tienes permiso para ejecutar este comando."));
                        return false;
                    }

                    if (args.length == 1) {

                        player.sendMessage(TextUtils.format("&eDebes introducir una palabra clave."));
                        player.sendMessage(TextUtils.format("&eEjemplo: &7/pdc locate beginning"));
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("beginning")) {

                        if (instance.getDay() < 40) {

                            player.sendMessage(TextUtils.format("&c&lERROR&7: &eNo existe el portal a The Beginning, por que estamos en el día &b" + instance.getDay()));
                            return false;
                        }

                        if (instance.getBeData() == null) {

                            player.sendMessage(TextUtils.format("&c&lERROR&7: &eNo pudimos encontrar The Beginning, por favor reinicia el servidor."));
                            player.sendMessage(TextUtils.format("&bPasos para generar la dimensión:"));
                            player.sendMessage(TextUtils.format("&e1. Debería generarse cuando un jugador entra en el día indicado (40)"));
                            return false;
                        }

                        if (!(player instanceof Player)) {

                            if (!instance.getBeData().generatedOverWorldBeginningPortal()) {

                                player.sendMessage(TextUtils.format("&c&lERROR&7: &eNo se ha generado el portal a The Beginning aún, reinicia el servidor."));
                                return false;
                            }

                            int x = (int) instance.getBeData().getOverWorldPortal().getX();
                            int y = (int) instance.getBeData().getOverWorldPortal().getY();
                            int z = (int) instance.getBeData().getOverWorldPortal().getZ();

                            player.sendMessage(TextUtils.format("&eCoordenadas del portal a The Beginning (overworld) &b" + x + " " + y + " " + z));
                        } else {

                            Player p = (Player) player;

                            if (p.getWorld().getName().equalsIgnoreCase(instance.world.getName())) {

                                if (!instance.getBeData().generatedOverWorldBeginningPortal()) {

                                    player.sendMessage(TextUtils.format("&c&lERROR&7: &eNo se ha generado el portal a The Beginning aún, reinicia el servidor."));
                                    return false;
                                }

                                int x = (int) instance.getBeData().getOverWorldPortal().getX();
                                int y = (int) instance.getBeData().getOverWorldPortal().getY();
                                int z = (int) instance.getBeData().getOverWorldPortal().getZ();

                                player.sendMessage(TextUtils.format("&eCoordenadas del portal a The Beginning (overworld) &b" + x + " " + y + " " + z));
                            } else if (p.getWorld().getName().equalsIgnoreCase("pdc_the_beginning")) {

                                if (!instance.getBeData().generatedBeginningPortal()) {

                                    player.sendMessage(TextUtils.format("&c&lERROR&7: &eNo se ha generado el portal a The Beginning aún, reinicia el servidor."));
                                    return false;
                                }

                                int x = (int) instance.getBeData().getBeginningPortal().getX();
                                int y = (int) instance.getBeData().getBeginningPortal().getY();
                                int z = (int) instance.getBeData().getBeginningPortal().getZ();

                                player.sendMessage(TextUtils.format("&eCoordenadas del portal a The Beginning (dimensión) &b" + x + " " + y + " " + z));
                            } else {

                                player.sendMessage(TextUtils.format("&c&lERROR&7: &eEste comando no puede ser ejecutado en tu mundo actual."));
                                player.sendMessage(TextUtils.format("&eRecuerda escribir correctamente el nombre de los mundos en config.yml"));
                            }
                        }

                    } else {

                        player.sendMessage(TextUtils.format("&cDebes introducir una palabra clave correcta."));
                        player.sendMessage(TextUtils.format("&eEjemplo: &7beginning"));
                        return false;
                    }

                } else if (args[0].equalsIgnoreCase("give")) {

                    if (!(sender instanceof Player)) {

                        sender.sendMessage(TextUtils.format("&cNecesitas ser un jugador."));
                        return false;
                    }

                    Player p = (Player) sender;

                    if (!p.hasPermission("permadeathcore.give")) {

                        p.sendMessage(TextUtils.format("&cNo tienes permisos."));
                        return false;
                    }

                    if (args.length == 1) {

                        p.sendMessage(TextUtils.format("&ePor favor introduce el ítem deseado"));
                        p.sendMessage(TextUtils.format("&eEjemplos: &7medalla - netheriteArmor - infernalArmor - infernalBlock - netheriteTools - lifeOrb - endRelic - beginningRelic"));
                        return false;
                    }

                    String s = args[1];

                    if (s.toLowerCase().equalsIgnoreCase("netheritearmor")) {

                        p.getInventory().addItem(NetheriteArmor.craftNetheriteHelmet());
                        p.getInventory().addItem(NetheriteArmor.craftNetheriteChest());
                        p.getInventory().addItem(NetheriteArmor.craftNetheriteLegs());
                        p.getInventory().addItem(NetheriteArmor.craftNetheriteBoots());

                        p.sendMessage(TextUtils.format("&eHas recibido la armadura de Netherite (comprueba no tener el inventario lleno)"));

                    } else if (s.toLowerCase().equalsIgnoreCase("medalla")) {

                        String medalla = TextUtils.format("&4&l[&c&l☠&4&l] &e&ki &r&6&lMedalla de Superviviente &e&ki &r&4&l[&c&l☠&4&l]");

                        p.getInventory().addItem(new ItemBuilder(Material.TOTEM_OF_UNDYING).setUnbrekeable(true).addItemFlag(ItemFlag.HIDE_UNBREAKABLE).setDisplayName(medalla).build());

                        p.sendMessage(TextUtils.format("&eHas recibido la medalla de superviviente (comprueba no tener el inventario lleno)"));

                    } else if (s.toLowerCase().equalsIgnoreCase("infernalarmor")) {

                        p.getInventory().addItem(InfernalNetherite.craftNetheriteHelmet());
                        p.getInventory().addItem(InfernalNetherite.craftNetheriteChest());
                        p.getInventory().addItem(InfernalNetherite.craftNetheriteLegs());
                        p.getInventory().addItem(InfernalNetherite.craftNetheriteBoots());

                        p.sendMessage(TextUtils.format("&eHas recibido la armadura de Netherite Infernal (comprueba no tener el inventario lleno)"));

                    } else if (s.toLowerCase().equalsIgnoreCase("netheritetools")) {

                        p.getInventory().addItem(PermadeathItems.craftNetheritePickaxe());
                        p.getInventory().addItem(PermadeathItems.craftNetheriteSword());
                        p.getInventory().addItem(PermadeathItems.craftNetheriteAxe());
                        p.getInventory().addItem(PermadeathItems.craftNetheriteShovel());
                        p.getInventory().addItem(PermadeathItems.craftNetheriteHoe());

                        p.sendMessage(TextUtils.format("&eHas recibido las herramientas de Netherite (comprueba no tener el inventario lleno)"));

                    } else if (s.toLowerCase().equalsIgnoreCase("infernalblock")) {
                        p.getInventory().addItem(PermadeathItems.craftInfernalNetheriteIngot());
                        p.sendMessage(TextUtils.format("&eHas recibido el Bloque de Netherite Infernal (comprueba no tener el inventario lleno)"));
                    } else if (s.toLowerCase().equalsIgnoreCase("lifeorb")) {
                        p.getInventory().addItem(PermadeathItems.createLifeOrb());
                        p.sendMessage(TextUtils.format("&eHas recibido el Orbe de Vida (comprueba no tener el inventario lleno)"));
                    } else if (s.toLowerCase().equalsIgnoreCase("endrelic")) {
                        p.getInventory().addItem(PermadeathItems.crearReliquia());
                        p.sendMessage(TextUtils.format("&eHas recibido la Reliquia del Fin (comprueba no tener el inventario lleno)"));
                    } else if (s.toLowerCase().equalsIgnoreCase("beginningrelic")) {
                        p.getInventory().addItem(PermadeathItems.createBeginningRelic());
                        p.sendMessage(TextUtils.format("&eHas recibido la Reliquia del Comienzo (comprueba no tener el inventario lleno)"));
                    } else {

                        p.sendMessage(TextUtils.format("&ePor favor introduce el ítem deseado"));
                        p.sendMessage(TextUtils.format("&eEjemplos: &7medalla - netheriteArmor - infernalArmor - infernalBlock - netheriteTools - lifeOrb - endRelic - beginningRelic"));
                    }

                } else if (args[0].equalsIgnoreCase("afk")) {

                    if (!player.hasPermission("permadeathcore.admin")) {
                        player.sendMessage(TextUtils.format("&cNo tienes permisos para ejecutar este comando."));
                        return false;
                    }

                    if (args.length == 1) {
                        player.sendMessage(TextUtils.format("&cLista de comandos disponibles para el sistema Anti-AFK"));
                        player.sendMessage(TextUtils.format("&7/pdc afk unban <jugador> &f&l- &cRevoca un baneo por AFK."));
                        player.sendMessage(TextUtils.format("&7/pdc afk bypass <add/remove> <jugador> &f&l- &cAgrega o elimina un jugador a la lista de personas inmunes."));
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("unban")) {

                        //player.sendMessage(instance.format("&"));

                        if (args.length == 2) {
                            player.sendMessage(TextUtils.format("&cPor favor, ingresa a un jugador."));
                            return false;
                        }

                        String paramPlayer = args[2];

                        if (Bukkit.getOfflinePlayer(paramPlayer) == null) {
                            player.sendMessage(TextUtils.format("&cJugador no encontrado"));
                            return false;
                        }

                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pardon" + paramPlayer);

                        PlayerDataManager data = new PlayerDataManager(paramPlayer, instance);
                        data.setLastDay(instance.getDay());

                        player.sendMessage(TextUtils.format("&aAhora el jugador &e" + paramPlayer + " &apodrá volver a jugar."));

                    } else if (args[1].equalsIgnoreCase("bypass")) {

                        if (args.length <= 3) {
                            player.sendMessage(TextUtils.format("&ePor favor, ingresa todos los argumentos del comando."));
                            return false;
                        }

                        String action = args[2];
                        String paramPlayer = args[3];

                        if (action.equalsIgnoreCase("add")) {

                            java.util.List<String> list = instance.getConfig().getStringList("AntiAFK.Bypass");

                            if (list.contains(paramPlayer)) {
                                player.sendMessage(TextUtils.format("&cEl jugador &e" + paramPlayer + " &cya se encuentra en la lista de jugadores inmunes."));
                                return false;
                            }

                            list.add(paramPlayer);

                            instance.getConfig().set("AntiAFK.Bypass", list);
                            instance.saveConfig();
                            instance.reloadConfig();

                            player.sendMessage(TextUtils.format("&aEl jugador &e" + paramPlayer + " &aha sido agregado a la lista de jugadores inmunes."));

                        } else if (action.equalsIgnoreCase("remove")) {
                            java.util.List<String> list = instance.getConfig().getStringList("AntiAFK.Bypass");

                            if (!list.contains(paramPlayer)) {
                                player.sendMessage(TextUtils.format("&cEl jugador &e" + paramPlayer + " &cno se encuentra en la lista de jugadores inmunes."));
                                return false;
                            }

                            list.remove(paramPlayer);

                            instance.getConfig().set("AntiAFK.Bypass", list);
                            instance.saveConfig();
                            instance.reloadConfig();

                            player.sendMessage(TextUtils.format("&aEl jugador &e" + paramPlayer + " &aha sido eliminado de la lista de jugadores inmunes."));
                        } else {
                            player.sendMessage(TextUtils.format("&cAcción &e" + action + " &cno reconocida."));
                        }

                    } else {
                        player.sendMessage(TextUtils.format("&cEse sub-comando no existe."));
                    }

                } else if (args[0].equalsIgnoreCase("storm")) {

                    if (!player.hasPermission("permadeathcore.admin")) {
                        player.sendMessage(TextUtils.format("&cNo tienes permisos para ejecutar este comando."));
                        return false;
                    }

                    if (args.length <= 2) {
                        player.sendMessage(TextUtils.format("&cLista de comandos disponibles para la tormenta."));
                        player.sendMessage(TextUtils.format("&7/pdc storm removeHours <horas> &f&l- &cElimina cantidad de horas de la tormenta."));
                        player.sendMessage(TextUtils.format("&7/pdc storm addHours <horas> &f&l- &cAgrega cantidad de horas a la tormenta."));
                        return false;
                    }

                    String operation = args[1];
                    int hours = 1;

                    try {
                        hours = Integer.parseInt(args[2]);

                        if (hours < 1) {
                            hours = 1;
                        }
                    } catch (Exception x) {
                        player.sendMessage(TextUtils.format("&cIngresa una cantidad válida."));
                    }

                    for (World w : Bukkit.getWorlds()
                            .stream()
                            .filter(world1 -> world1.getEnvironment() == World.Environment.NORMAL)
                            .collect(Collectors.toList())) {

                        if (operation.equalsIgnoreCase("addHours")) {
                            int stormDuration = w.getWeatherDuration();
                            int stormTicksToSeconds = stormDuration / 20;
                            long stormIncrement = stormTicksToSeconds + hours * 3600;
                            int intsTicks = (int) hours * 3600;
                            int inc = (int) stormIncrement;

                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:weather thunder");

                            if (w.hasStorm() || w.isThundering()) {
                                instance.world.setWeatherDuration(inc * 20);
                            } else {
                                instance.world.setWeatherDuration(intsTicks * 20);
                            }

                            player.sendMessage(TextUtils.format("&aOperación completada exitosamente."));
                        } else {

                            if (w.hasStorm() || w.isThundering()) {
                                int stormDuration = w.getWeatherDuration();
                                int stormTicksToSeconds = stormDuration / 20;
                                int newStormTime = Math.max(1, stormTicksToSeconds - hours * 3600);
                                instance.world.setWeatherDuration(newStormTime * 20);
                            } else {
                                player.sendMessage(TextUtils.format("&cNo hay ninguna tormenta en marcha."));
                            }
                        }
                    }

                } else {
                    sendHelp(player);
                }
            }
        }

        return false;
    }

    private void sendHelp(CommandSender sender) {

        sender.sendMessage(instance.prefix + ChatColor.RED + "Comandos disponibles:");
        sender.sendMessage(ChatColor.RED + "/pdc idioma <es, en>" + ChatColor.GRAY + ChatColor.ITALIC + "(Cambia tu idioma)");
        sender.sendMessage(ChatColor.RED + "/pdc dias " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra el día en el que está el plugin)");
        sender.sendMessage(ChatColor.RED + "/pdc duracion " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra la duración de la tormenta)");
        sender.sendMessage(ChatColor.RED + "/pdc cambios " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra los cambios de dificultad disponibles)");

        if (sender instanceof Player) {

            sender.sendMessage(ChatColor.RED + "/pdc mensaje <mensaje> " + ChatColor.GRAY + ChatColor.ITALIC + "(Cambia tu mensaje de muerte)");
            sender.sendMessage(ChatColor.RED + "/pdc awake " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra el tiempo despierto)");
        }
        sender.sendMessage(ChatColor.RED + "/pdc info " + ChatColor.GRAY + ChatColor.ITALIC + "(Información general)");
        sender.sendMessage(ChatColor.RED + "/pdc discord " + ChatColor.GRAY + ChatColor.ITALIC + "(Discord oficial del plugin)");

        if (sender.hasPermission("permadeathcore.admin")) {

            sender.sendMessage("");
            sender.sendMessage(instance.prefix + ChatColor.RED + "Comandos de administrador:");
            sender.sendMessage(ChatColor.RED + "/pdc debug " + ChatColor.GRAY + ChatColor.ITALIC + "(Información importante para el soporte)");
            sender.sendMessage(ChatColor.RED + "/pdc reload " + ChatColor.GRAY + ChatColor.ITALIC + "(Recarga el archivo config.yml)");
            sender.sendMessage(ChatColor.RED + "/pdc afk " + ChatColor.GRAY + ChatColor.ITALIC + "(Administra el sistema Anti-AFK)");
            sender.sendMessage(ChatColor.RED + "/pdc storm " + ChatColor.GRAY + ChatColor.ITALIC + "(Administra la tormenta)");
            sender.sendMessage(ChatColor.RED + "/pdc cambiarDia <dia> " + ChatColor.GRAY + ChatColor.ITALIC + "(Cambia el día actual, pd: puede que requiera un reinicio)");
            sender.sendMessage(ChatColor.RED + "/pdc speedrun " + ChatColor.GRAY + ChatColor.ITALIC + "(Comandos del modo SpeedRun, cada día es una hora)");
            sender.sendMessage(ChatColor.RED + "/pdc beginning " + ChatColor.GRAY + ChatColor.ITALIC + "(Comandos de TheBeginning)");
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "/pdc give <medalla, netheriteArmor, infernalArmor, infernalBlock, netheriteTools> " + ChatColor.GRAY + ChatColor.ITALIC + "(Obtén ítems especiales de Permadeath)");
            }
            sender.sendMessage(ChatColor.RED + "/pdc event <shulkershell, lifeorb> " + ChatColor.GRAY + ChatColor.ITALIC + "(Comienza un evento)");
            sender.sendMessage(ChatColor.RED + "/pdc locate <beginning> " + ChatColor.GRAY + ChatColor.ITALIC + "(Localiza el portal a The Beginning)");
        }
    }
}
