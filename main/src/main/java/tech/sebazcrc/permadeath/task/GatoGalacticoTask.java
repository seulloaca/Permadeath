package tech.sebazcrc.permadeath.task;

import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.VersionManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

public class GatoGalacticoTask extends BukkitRunnable {

    private Location cat;
    private Main main;

    int time = 5;

    public GatoGalacticoTask(Location cat, Main main) {
        this.cat = cat;
        this.main = main;
    }

    @Override
    public void run() {

        if (time > 0) {

            Bukkit.broadcastMessage(TextUtils.format("&eUn gato galáctico invocará un mob al azar en: &b" + time));

            for (Player all : Bukkit.getOnlinePlayers()) {

                all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100.0F, 100.0F);
            }

            time = time - 1;
        }

        if (time == 0) {

            String entidad = "";

            ArrayList<EntityType> entity = new ArrayList<>();
            entity.add(EntityType.CAT);
            entity.add(EntityType.PUFFERFISH);
            entity.add(EntityType.RAVAGER);
            entity.add(EntityType.ENDER_DRAGON);
            entity.add(EntityType.SKELETON);
            entity.add(EntityType.SLIME);
            entity.add(EntityType.MAGMA_CUBE);
            entity.add(EntityType.WITCH);
            entity.add(EntityType.SPIDER);
            entity.add(EntityType.SILVERFISH);
            entity.add(EntityType.ENDERMITE);
            entity.add(EntityType.PHANTOM);
            entity.add(EntityType.GHAST);
            entity.add(EntityType.CREEPER);
            entity.add(EntityType.SHULKER);
            entity.add(EntityType.GIANT);
            entity.add(EntityType.WITHER_SKELETON);

            int c = new Random().nextInt(entity.size());
            EntityType type = entity.get(c);

            SplittableRandom random = new SplittableRandom();

            if (type == EntityType.CAT) {

                entidad = "Gato Supernova";

                Cat gato = (Cat) cat.getWorld().spawnEntity(cat, EntityType.CAT);
                gato.setAdult();
                gato.setCustomName(TextUtils.format("&6" + entidad));
                Main.getInstance().getSpawnListener().explodeCat(gato);

            } else if (type == EntityType.PUFFERFISH) {

                entidad = "Pufferfish invulnerable";

                PufferFish ent = (PufferFish) cat.getWorld().spawnEntity(cat, EntityType.PUFFERFISH);

            } else if (type == EntityType.RAVAGER) {

                EntityType PIGMAN;

                if (VersionManager.isRunningPostNetherUpdate()) {
                    PIGMAN = EntityType.valueOf("ZOMBIFIED_PIGLIN");
                } else {
                    PIGMAN = EntityType.valueOf("PIG_ZOMBIE");
                }

                entidad = "Ultra Ravager";

                Ravager ravager = cat.getWorld().spawn(cat, Ravager.class);
                LivingEntity carlos = (LivingEntity) cat.getWorld().spawnEntity(cat, PIGMAN);
                Villager jess = cat.getWorld().spawn(cat, Villager.class);

                carlos.addPassenger(jess);
                ravager.addPassenger(carlos);

                Main.instance.getNmsAccessor().setMaxHealth(jess, 500.0D, true);

                Main.instance.getNmsAccessor().setMaxHealth(carlos, 150.0D, true);

                Main.instance.getNmsAccessor().setMaxHealth(ravager, 240.0D, true);

                jess.setCustomName(ChatColor.GREEN + "Jess la Emperatriz");
                carlos.setCustomName(ChatColor.GREEN + "Carlos el Esclavo");
                ravager.setCustomName(ChatColor.GREEN + "Ultra Ravager");

                jess.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_APPLE, 2));
                jess.getEquipment().setItemInMainHandDropChance(0);

                carlos.getEquipment().setItemInMainHand(new ItemStack(Material.GOLD_INGOT, 32));
                carlos.getEquipment().setItemInMainHandDropChance(0);

                ravager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                ravager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                ravager.getPersistentDataContainer().set(new NamespacedKey(Main.instance, "ultra_ravager"), PersistentDataType.BYTE, (byte) 1);


            } else if (type == EntityType.ENDER_DRAGON) {

                entidad = "Permadeath Demon";
                EnderDragon demon = (EnderDragon) cat.getWorld().spawnEntity(cat, EntityType.ENDER_DRAGON);

                demon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
                demon.setAI(true);

                demon.setCustomName(TextUtils.format("&6&lPERMADEATH DEMON"));
                main.getNmsAccessor().setMaxHealth(demon, 1350.0D, true);

            } else if (type == EntityType.SKELETON) {

                entidad = "Esqueleto de Clase";
                Main.instance.getNmsHandler().spawnNMSEntity("Skeleton", EntityType.SKELETON, cat, CreatureSpawnEvent.SpawnReason.NATURAL);

            } else if (type == EntityType.SLIME) {

                entidad = "Giga Slime";
                cat.getWorld().spawnEntity(cat, EntityType.SLIME);

            } else if (type == EntityType.MAGMA_CUBE) {

                entidad = "Giga MagmaCube";
                cat.getWorld().spawnEntity(cat, EntityType.MAGMA_CUBE);

            } else if (type == EntityType.WITCH) {

                entidad = "Bruja Imposible";
                cat.getWorld().spawnEntity(cat, EntityType.WITCH);

            } else if (type == EntityType.PHANTOM) {

                entidad = "Giga Phantom";
                Main.instance.getNmsHandler().spawnNMSEntity("Phantom", EntityType.PHANTOM, cat, CreatureSpawnEvent.SpawnReason.NATURAL);

            } else if (type == EntityType.SPIDER) {

                entidad = "Araña con Efectos";
                cat.getWorld().spawnEntity(cat, EntityType.CAVE_SPIDER);

            } else if (type == EntityType.SILVERFISH) {

                entidad = "Silverfish de la Muerte";
                cat.getWorld().spawnEntity(cat, EntityType.SILVERFISH);

            } else if (type == EntityType.ENDERMITE) {

                entidad = "Endermite Misterioso";
                cat.getWorld().spawnEntity(cat, EntityType.ENDERMITE);

            } else if (type == EntityType.SHULKER) {

                entidad = "Shulker Tnt";
                cat.getWorld().spawnEntity(cat, EntityType.SHULKER);

            } else if (type == EntityType.GHAST) {

                int r = random.nextInt(3) + 1;

                if (r == 1) {

                    entidad = "Ender Ghast";

                    main.getNmsHandler().spawnCustomGhast(cat, CreatureSpawnEvent.SpawnReason.CUSTOM, true);
                }

                if (r == 2) {

                    entidad = "Ghast Demoníaco";

                    Ghast GhastDemon = (Ghast) cat.getWorld().spawnEntity(cat, EntityType.GHAST);

                    Double HPGenerator = ThreadLocalRandom.current().nextDouble(40, 60 + 1);
                    main.getNmsAccessor().setMaxHealth(GhastDemon, HPGenerator, true);
                    GhastDemon.setHealth(HPGenerator);
                    GhastDemon.setCustomName(ChatColor.GOLD + "Ghast Demoníaco");
                }

                if (r == 3) {

                    entidad = "Demonio Flotante";

                    Double HPGenerator = ThreadLocalRandom.current().nextDouble(40, 60 + 1);
                    LivingEntity GhastDemon = (LivingEntity) cat.getWorld().spawnEntity(cat, EntityType.GHAST);
                    main.getNmsAccessor().setMaxHealth(GhastDemon, HPGenerator, true);
                    GhastDemon.setHealth(HPGenerator);
                    GhastDemon.setCustomName(ChatColor.GOLD + "Demonio flotante");
                }
            } else if (type == EntityType.CREEPER) {

                int i;

                if (Main.instance.getDay() < 60) {
                    i = random.nextInt(3) + 1;
                } else {
                    i = random.nextInt(2) + 1;
                }

                if (i == 1) {
                    entidad = "Ender Quantum Creeper";
                    main.getFactory().spawnEnderQuantumCreeper(cat, null);
                }

                if (i == 2) {
                    entidad = "Quantum Creeper";
                    main.getFactory().spawnQuantumCreeper(cat, null);
                }

                if (i == 3) {
                    entidad = "Ender Creeper";
                    main.getFactory().spawnEnderCreeper(cat, null);
                }
            }

            if (type == EntityType.GIANT) {

                entidad = "Gigante";
                Main.instance.getNmsHandler().spawnNMSCustomEntity("CustomGiant", EntityType.GIANT, cat, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

            if (type == EntityType.WITHER_SKELETON) {

                entidad = "Wither Skeleton Emperador";

                WitherSkeleton skeleton = (WitherSkeleton) Main.instance.getNmsHandler().spawnNMSEntity("SkeletonWither", EntityType.WITHER_SKELETON, cat, CreatureSpawnEvent.SpawnReason.CUSTOM);
                EntityEquipment eq = skeleton.getEquipment();

                Main.instance.getNmsAccessor().setMaxHealth(skeleton, 80.0D, true);

                skeleton.setCustomName(TextUtils.format("&6Wither Skeleton Emperador"));
                skeleton.setCollidable(false);

                ItemStack i = new ItemStack(Material.BLACK_BANNER, 1);
                BannerMeta m = (BannerMeta) i.getItemMeta();
                java.util.List<Pattern> patterns = new ArrayList<>();

                patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRAIGHT_CROSS));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.BRICKS));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
                patterns.add(new Pattern(DyeColor.YELLOW, PatternType.FLOWER));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
                patterns.add(new Pattern(DyeColor.RED, PatternType.GRADIENT_UP));
                m.setPatterns(patterns);
                i.setItemMeta(m);

                eq.setHelmet(i);
                eq.setHelmetDropChance(0);
                eq.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
                eq.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
                eq.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
                eq.setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_KNOCKBACK, 5).addEnchant(Enchantment.ARROW_DAMAGE, 100).build());
                eq.setItemInMainHandDropChance(0);
            }

            Bukkit.broadcastMessage(TextUtils.format("&eUn gato galáctico ha invicado un(a) &c&l" + entidad + " &7(" + cat.getX() + ", " + cat.getY() + ", " + cat.getZ()));
            this.cancel();
        }
    }
}
