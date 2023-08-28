package tech.sebazcrc.permadeath.event.entity;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.NMS;
import tech.sebazcrc.permadeath.util.item.NetheriteArmor;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.util.lib.LeatherArmorBuilder;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.VersionManager;
import tech.sebazcrc.permadeath.task.GatoGalacticoTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SpawnListener implements Listener {

    private Main plugin;

    private SplittableRandom random;
    private ArrayList<LivingEntity> gatosSupernova;

    // 1.16
    private EntityType PIGMAN;
    private Class pigmanClass;

    // Spawns
    private boolean optimizeSpawns;

    public SpawnListener(Main instance) {

        this.plugin = instance;
        this.random = new SplittableRandom();
        this.gatosSupernova = new ArrayList<>();
        this.optimizeSpawns = instance.getConfig().getBoolean("Toggles.Optimizar-Mob-Spawns");

        if (VersionManager.isRunningPostNetherUpdate()) {
            this.PIGMAN = EntityType.valueOf("ZOMBIFIED_PIGLIN");
        } else {
            this.PIGMAN = EntityType.valueOf("PIG_ZOMBIE");
        }
        this.pigmanClass = PIGMAN.getEntityClass();
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType eventEntityType = event.getEntityType();
        Location location = event.getLocation();
        World world = location.getWorld();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if (optimizeSpawns && world.getEnvironment() == World.Environment.NORMAL) {
            if (reason == CreatureSpawnEvent.SpawnReason.NATURAL || reason == CreatureSpawnEvent.SpawnReason.CUSTOM) {
                if (eventEntityType == EntityType.COD || eventEntityType == EntityType.VINDICATOR || eventEntityType == EntityType.GUARDIAN || eventEntityType == EntityType.ELDER_GUARDIAN || eventEntityType == PIGMAN || eventEntityType == EntityType.EVOKER || eventEntityType == EntityType.CAVE_SPIDER || eventEntityType == EntityType.SKELETON || eventEntityType == EntityType.BLAZE) {
                    if (Arrays.stream(location.getChunk().getEntities())
                            .filter(entity1 -> entity1.getType() == eventEntityType)
                            .map(eventEntityType.getEntityClass()::cast)
                            .collect(Collectors.toList()).size() >= 8) {
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (optimizeSpawns && world.getEnvironment() != World.Environment.THE_END && entity instanceof Monster && world.getEntitiesByClass(Monster.class).size() >= 220) {
            event.setCancelled(true);
        }

        if (event.isCancelled()) return;

        spawnBeginningMob(event);
        spawnNetheriteMob(event);

        plugin.deathTrainEffects(entity);

        if (entity instanceof Spider || eventEntityType == EntityType.SKELETON) {
            if (plugin.getConfig().getBoolean("Toggles.Spider-Effect") && entity instanceof Spider) {
                addMobEffects(entity, 100);
            }
            if (plugin.getDay() >= 20) {
                if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                spawnSkeletonClass(entity, location);
            }
        }

        if (plugin.getDay() >= 20) {

            if (eventEntityType == EntityType.PHANTOM) {

                Phantom phantom = (Phantom) entity;
                int pSize = (plugin.getDay() < 50 ? 9 : 18);

                plugin.getNmsAccessor().setMaxHealth(phantom, plugin.getNmsAccessor().getMaxHealth(phantom) * 2, true);

                if (plugin.getDay() >= 40) {
                    Skeleton skeleton = (Skeleton) plugin.getNmsHandler().spawnNMSEntity("Skeleton", EntityType.SKELETON, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                    phantom.addPassenger(skeleton);
                }

                if (plugin.getDay() >= 50) {
                    int r = (plugin.getDay() < 60 ? 1 : 25);
                    if (random.nextInt(101) <= r) {
                        for (int i = 0; i < 4; i++) {
                            plugin.getNmsHandler().spawnCustomGhast(event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM, true);
                        }
                    }
                    addMobEffects(entity, 3);
                }
                phantom.setSize(pSize);
            }

            if (event.getEntityType() == PIGMAN) { // Pigman = 1.15 y 1.14 PIG_ZOMBIE | 1.16 ZOMBIFIED_PIGLIN

                if (plugin.getDay() >= 60) {

                    event.setCancelled(true);
                    return;
                }

                LivingEntity pigman = entity;

                try {
                    Method m = pigman.getClass().getDeclaredMethod("setAngry", boolean.class);
                    m.setAccessible(true);
                    m.invoke(pigman, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (plugin.getDay() >= 30 && plugin.getDay() < 40) {

                    EntityEquipment eq = pigman.getEquipment();

                    eq.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    eq.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    eq.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    eq.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                }

                if (plugin.getDay() >= 40) {

                    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                    if (plugin.getDay() >= 60 && world.getEnvironment() == World.Environment.NETHER) {
                        event.setCancelled(true);
                        return;
                    }

                    int randomProb = random.nextInt(99) + 1;
                    int cantidad = (plugin.getDay() < 50 ? 5 : 20);

                    if (randomProb <= cantidad) {

                        EntityEquipment eq = pigman.getEquipment();
                        int clase = ThreadLocalRandom.current().nextInt(1, 5 + 1);
                        if (clase == 1) {
                            spawnUltraRavager(event);
                        }

                        if (clase == 2) {

                            ItemStack helmet = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1).setColor(Color.YELLOW).build();
                            ItemStack che = new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.YELLOW).build();
                            ItemStack le = new LeatherArmorBuilder(Material.LEATHER_LEGGINGS, 1).setColor(Color.YELLOW).build();
                            ItemStack bo = new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.YELLOW).build();

                            eq.setHelmet(helmet);
                            eq.setChestplate(che);
                            eq.setLeggings(le);
                            eq.setBoots(bo);

                            plugin.getNmsAccessor().setMaxHealth(pigman, pigman.getHealth(), true);
                            LivingEntity bee = (LivingEntity) plugin.getNmsHandler().spawnNMSCustomEntity("SpecialBee", EntityType.valueOf("BEE"), event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);

                            pigman.setCollidable(true);
                            bee.setCollidable(true);

                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {

                                    pigman.teleport(bee.getLocation());
                                    bee.addPassenger(pigman);
                                }
                            }, 10L);
                        }

                        if (clase == 3) {

                            pigman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0D);
                            pigman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));

                            Ghast ghast = (Ghast) plugin.getNmsHandler().spawnCustomGhast(event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM, false);
                            ghast.getPersistentDataContainer().set(new NamespacedKey(plugin, "tp_ghast"), PersistentDataType.BYTE, (byte) 1);

                            ghast.setCollidable(true);
                            pigman.setCollidable(true);

                            ghast.addPassenger(pigman);
                        }

                        if (clase == 4) {

                            MagmaCube cube = (MagmaCube) plugin.getNmsHandler().spawnNMSEntity("MagmaCube", EntityType.MAGMA_CUBE, event.getLocation(), CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
                            cube.setSize(1);

                            plugin.getNmsAccessor().setMaxHealth(pigman, 1.0D, true);
                            pigman.setCollidable(false);
                            cube.addPassenger(pigman);
                        }

                        if (clase == 5) {

                            ItemStack helmet = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1).setColor(Color.GRAY).build();
                            ItemStack che = new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.GRAY).build();
                            ItemStack le = new LeatherArmorBuilder(Material.LEATHER_LEGGINGS, 1).setColor(Color.GRAY).build();
                            ItemStack bo = new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.GRAY).build();

                            eq.setHelmet(helmet);
                            eq.setChestplate(che);
                            eq.setLeggings(le);
                            eq.setBoots(bo);

                            plugin.getNmsAccessor().setMaxHealth(pigman, pigman.getHealth(), true);
                            Pig pig = (Pig) plugin.getNmsHandler().spawnNMSCustomEntity("SpecialPig", EntityType.PIG, event.getLocation(), CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);

                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {

                                    pig.setSaddle(true);
                                    pigman.teleport(pig.getLocation());
                                    pig.addPassenger(pigman);
                                }
                            }, 10L);
                        }
                    } else {

                        EntityEquipment eq = pigman.getEquipment();
                        eq.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                        eq.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        eq.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                        eq.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    }
                }
            }
        }

        if (plugin.getDay() >= 25) {

            if (entity instanceof Ravager) {
                Ravager ravager = (Ravager) entity;

                if (plugin.getDay() < 40) {
                    ravager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    ravager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                    ravager.setRemoveWhenFarAway(true);
                }
            }

            if (entity.getEquipment() != null && (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.ZOMBIE)) {
                ItemStack[] contents = entity.getEquipment().getArmorContents().clone();

                int index = 0;
                for (ItemStack armor : contents) {
                    if (armor != null && armor.getType().name().toLowerCase().contains("leather_") && !armor.getItemMeta().isUnbreakable()) {
                        contents[index] = null;
                    }
                    index++;
                }
                entity.getEquipment().setArmorContents(contents);
            }
        }

        if (plugin.getDay() >= 30) {
            if (entity instanceof Silverfish || entity instanceof Endermite) addMobEffects(entity, 100);
            if (entity instanceof Enderman) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, plugin.getDay() < 60 ? 1 : 9));

                if (plugin.getDay() >= 40) {
                    if (world.getEnvironment() == World.Environment.NETHER) {
                        event.setCancelled(true);
                        Creeper c = plugin.getFactory().spawnEnderCreeper(event.getLocation(), null);
                        c.setMetadata("nether_creeper", new FixedMetadataValue(plugin, true));
                    }

                    if (random.nextInt(100) <= 4 && world.getEnvironment() == World.Environment.NORMAL) {
                        NMS.spawnDeathModule(event.getLocation());
                        event.setCancelled(true);
                    }
                }
            }

            if (entity instanceof Squid) {
                event.setCancelled(true);

                if (location.getWorld().getNearbyEntities(location, 20, 20, 20).stream().filter(entity1 -> entity1 instanceof Guardian).map(Guardian.class::cast).collect(Collectors.toList()).size() < 20) {
                    Guardian g = (Guardian) entity.getWorld().spawnEntity(event.getLocation(), EntityType.GUARDIAN);
                    g.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                }
            }

            if (entity instanceof IronGolem) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
                if (plugin.getDay() >= 40) {

                    entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, plugin.getDay() < 60 ? 0 : 3));

                    if (plugin.getDay() >= 50) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, plugin.getDay() < 60 ? 1 : 3));
                    }
                }
            }

            if (entity instanceof Bat) {
                event.setCancelled(true);
                if (location.getWorld().getLivingEntities().stream().filter(entity1 -> entity1 instanceof Blaze).map(Blaze.class::cast).collect(Collectors.toList()).size() < 30) {
                    Blaze g = (Blaze) entity.getWorld().spawnEntity(event.getLocation(), EntityType.BLAZE);
                    g.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                }
            }

            if (entity instanceof Creeper) {
                Creeper c = (Creeper) entity;
                c.setPowered(true);

                if (plugin.getDay() >= 40) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));

                    if (plugin.getDay() >= 50) {

                        if (plugin.getDay() < 60) {
                            int r = random.nextInt(10);
                            if (r <= 1) {
                                plugin.getFactory().spawnEnderCreeper(location, c);

                            } else {
                                plugin.getFactory().spawnQuantumCreeper(location, c);
                            }
                        } else {
                            plugin.getFactory().spawnEnderQuantumCreeper(location, c);
                            c.setMaxFuseTicks(c.getMaxFuseTicks() / 2);
                        }
                    }
                }
            }

            if (entity instanceof Pillager) {
                Pillager p = (Pillager) entity;
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

                p.getEquipment().setItemInMainHand(new ItemBuilder(Material.CROSSBOW).addEnchant(Enchantment.QUICK_CHARGE, 4).build());
                p.getEquipment().setItemInMainHandDropChance(0);

                if (plugin.getDay() >= 50) {
                    int prob = random.nextInt(100);
                    if (prob == 0) {
                        event.setCancelled(true);
                        event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.EVOKER);
                    }
                }
            }
        }

        if (plugin.getDay() >= 40) {
            if (entity.getType() == EntityType.GUARDIAN) {
                if (plugin.getDay() >= 60) {
                    event.setCancelled(true);

                    if (location.getWorld().getNearbyEntities(location, 20, 20, 20).stream().filter(entity1 -> entity1 instanceof ElderGuardian).map(ElderGuardian.class::cast).collect(Collectors.toList()).size() < 5) {
                        event.getLocation().getWorld().spawn(event.getLocation(), ElderGuardian.class);
                    }
                    return;
                }
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
            }

            if (entity.getType() == EntityType.SPIDER) {
                event.setCancelled(true);
                plugin.getNmsHandler().spawnNMSEntity("CaveSpider", EntityType.CAVE_SPIDER, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                entity.setCustomName(TextUtils.format("&6Araña inmortal"));
            }

            if (entity.getType() == EntityType.ZOMBIE) {
                event.setCancelled(true);
                if (world.getNearbyEntities(location, 15, 15, 15).stream().filter(entity1 -> entity1 instanceof Vindicator).map(Vindicator.class::cast).collect(Collectors.toList()).size() < 5) {
                    Vindicator vindicator = (Vindicator) event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.VINDICATOR);
                    vindicator.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                    plugin.getNmsAccessor().setMaxHealth(vindicator, plugin.getNmsAccessor().getMaxHealth(vindicator) * 2, true);
                }
            }

            if (eventEntityType == EntityType.WOLF) {
                event.setCancelled(true);
                plugin.getNmsHandler().spawnNMSEntity("Cat", EntityType.CAT, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
            }

            if (eventEntityType == EntityType.CAT || eventEntityType == EntityType.OCELOT) {
                if (plugin.getDay() < 50) {
                    entity.setCustomName(TextUtils.format("&6Gato Supernova"));
                    explodeCat(entity);
                } else {
                    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
                        explodeCat(event.getEntity());
                    } else {
                        event.getEntity().setCustomName(TextUtils.format("&6Gato Galáctico"));
                    }
                }
            }

            if (entity instanceof Cow || entity instanceof Sheep || entity instanceof Pig || entity instanceof MushroomCow) {
                if (!event.getLocation().getWorld().getName().equalsIgnoreCase(plugin.world.getName())) return;

                if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                    event.setCancelled(true);
                    plugin.getNmsHandler().spawnNMSEntity("Ravager", EntityType.RAVAGER, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                }

                if (plugin.getDay() >= 50) {
                    event.setCancelled(true);
                    Ravager ultraRavager = (Ravager) plugin.getNmsHandler().spawnNMSCustomEntity("UltraRavager", EntityType.RAVAGER, event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                    ultraRavager.setCustomName(TextUtils.format("&6Ultra Ravager"));
                    ultraRavager.setCustomNameVisible(true);
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                    plugin.getNmsAccessor().setMaxHealth(ultraRavager, 500.0D, true);
                }
            }

            if (entity instanceof Chicken) {
                if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                    event.setCancelled(true);
                    plugin.getNmsHandler().spawnNMSEntity("Ravager", EntityType.RAVAGER, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                    return;
                }
                if (plugin.getDay() >= 50) {
                    event.setCancelled(true);
                    event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.SILVERFISH);
                }
            }

            if (entity instanceof Witch) {

                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                plugin.getNmsAccessor().setMaxHealth(entity, entity.getHealth() * 2, true);

                entity.setCustomName(TextUtils.format("&6Bruja Imposible"));
            }
        }

        if (plugin.getDay() >= 50) {

            if (entity instanceof Vindicator) {
                Illager i = (Illager) entity;
                i.getEquipment().setItemInMainHand(new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.DAMAGE_ALL, 5).build());
            }

            if (event.getEntityType() == EntityType.VEX) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
            }

            if (event.getEntityType() == EntityType.BLAZE) {
                plugin.getNmsAccessor().setMaxHealth(entity, 200.0D, true);
            }

            if (entity.getType() == EntityType.COD) {
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                Cod cod = (Cod) entity;
                cod.getEquipment().setItemInMainHand(new ItemBuilder(Material.WOODEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 50).addEnchant(Enchantment.KNOCKBACK, 100).build());
                cod.getEquipment().setItemInMainHandDropChance(0.0f);
                cod.setCustomName(TextUtils.format("&6Bacalao de la Muerte"));
            }

            if (entity.getType() == EntityType.DROWNED) {
                entity.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
            }

            if (entity.getType() == EntityType.SALMON) {
                event.setCancelled(true);
                if (world.getNearbyEntities(location, 15, 15, 15).stream().filter(entity1 -> entity1 instanceof PufferFish).map(PufferFish.class::cast).collect(Collectors.toList()).size() < 2) {
                    event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.PUFFERFISH);
                }
            }

            if (entity.getType() == EntityType.PUFFERFISH) {
                PufferFish fish = (PufferFish) entity;
                fish.setCustomName(TextUtils.format("&6Pufferfish invulnerable"));
                fish.setInvulnerable(true);
            }

            if (entity.getType() == EntityType.WITHER_SKELETON) {

                WitherSkeleton skeleton = (WitherSkeleton) entity;
                EntityEquipment eq = skeleton.getEquipment();

                int prob = random.nextInt((plugin.getDay() < 60 ? 50 : 13)) + 1;

                if (skeleton.getWorld().getEnvironment() == World.Environment.NETHER && prob == 5) {

                    plugin.getNmsAccessor().setMaxHealth(skeleton, 80.0D, true);

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

                    event.setCancelled(false);
                }
            }

            if (entity.getType() == EntityType.ZOMBIE) {

                boolean b = false;
                try {
                    b = (boolean) World.class.getDeclaredMethod("getBiome", int.class, int.class, int.class).invoke(event.getLocation().getWorld(), (int) event.getLocation().getX(), (int) event.getLocation().getY(), (int) event.getLocation().getZ());
                } catch (Exception x) {
                }

                if (b) {

                    int prob = 1;

                    if (plugin.getDay() < 60) {
                        prob = random.nextInt(500) + 1;
                    } else {

                        prob = random.nextInt(125) + 1;
                    }

                    if (event.getLocation().getBlock().getBiome() == Biome.PLAINS) {

                        if (prob == 5) {

                            plugin.getNmsHandler().spawnNMSCustomEntity("CustomGiant", EntityType.GIANT, event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                            event.setCancelled(true);
                        }
                    }
                }
            }

            if (entity instanceof Ravager) {
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                if (plugin.getDay() >= 50) {

                    if (entity.getWorld().getName().equalsIgnoreCase(plugin.world.getName())) {

                        Ravager ultraRavager = (Ravager) plugin.getNmsHandler().spawnNMSCustomEntity("UltraRavager", EntityType.RAVAGER, event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                        ultraRavager.setCustomName(TextUtils.format("&6Ultra Ravager"));
                        ultraRavager.setCustomNameVisible(true);
                        ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                        ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                        plugin.getNmsAccessor().setMaxHealth(ultraRavager, 500.0D, true);

                        event.setCancelled(true);
                    }
                }
            }
        }

        if (plugin.getDay() >= 60) {

            if (event.getEntityType() == EntityType.VEX) {
                Vex v = (Vex) entity;
                v.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2));
                v.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(7.0D);
            }

            if (event.getEntityType() == EntityType.VILLAGER) {
                event.setCancelled(true);
                if (random.nextBoolean()) {
                    event.getLocation().getWorld().spawn(event.getLocation(), Vindicator.class);
                } else {
                    event.getLocation().getWorld().spawn(event.getLocation(), Vex.class);
                }
            }

            if (eventEntityType == EntityType.VINDICATOR) {
                if (random.nextBoolean()) {
                    event.setCancelled(true);
                    if (world.getNearbyEntities(location, 15, 15, 15).stream().filter(entity1 -> entity1 instanceof Evoker).map(Evoker.class::cast).collect(Collectors.toList()).size() < 5) {
                        Evoker evoker = (Evoker) event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.EVOKER);
                        plugin.getNmsAccessor().setMaxHealth(evoker, plugin.getNmsAccessor().getMaxHealth(evoker) * 2, true);
                        evoker.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (plugin.getConfig().getBoolean("Toggles.Replace-Mobs-On-Chunk-Load")) {
            for (LivingEntity liv : Arrays.stream(e.getChunk().getEntities()).filter(entity -> entity instanceof LivingEntity).map(LivingEntity.class::cast).collect(Collectors.toList())) {
                applyDayChanges(liv);
            }
        }

        if (plugin.getDay() >= 40 && plugin.getDay() < 50) {
            for (LivingEntity cats : Arrays.stream(e.getChunk().getEntities()).filter(entity -> isACat(entity)).map(LivingEntity.class::cast).collect(Collectors.toList())) {
                cats.setCustomName(TextUtils.format("&6Gato Supernova"));
                explodeCat(cats);
            }

            for (Wolf wolf : Arrays.stream(e.getChunk().getEntities()).filter(entity -> entity instanceof Wolf).map(Wolf.class::cast).collect(Collectors.toList())) {
                Cat cat = wolf.getWorld().spawn(wolf.getLocation().clone(), Cat.class);
                wolf.remove();
                cat.setAdult();
                cat.setCustomName(TextUtils.format("&6Gato Supernova"));
                explodeCat(cat);
            }
        }
    }

    private boolean isACat(Entity entity) {
        return entity.getType() == EntityType.CAT || entity.getType() == EntityType.OCELOT;
    }

    public void applyDayChanges(LivingEntity entity) {

        if (plugin.getDay() >= 30) {
            if (entity instanceof Squid) {
                entity.remove();
                Guardian g = (Guardian) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.GUARDIAN);
                g.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            }
            if (entity instanceof Bat) {
                entity.remove();
                Blaze g = (Blaze) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.BLAZE);
                g.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
            }
        }

        if (plugin.getDay() >= 40) {
            if (entity instanceof Cow || entity instanceof Sheep || entity instanceof Pig || entity instanceof MushroomCow) {
                if (!entity.getLocation().getWorld().getName().equalsIgnoreCase(plugin.world.getName())) return;

                if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                    entity.remove();
                    plugin.getNmsHandler().spawnNMSEntity("Ravager", EntityType.RAVAGER, entity.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                }

                if (plugin.getDay() >= 50) {
                    entity.remove();
                    Ravager ultraRavager = (Ravager) plugin.getNmsHandler().spawnNMSCustomEntity("UltraRavager", EntityType.RAVAGER, entity.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                    ultraRavager.setCustomName(TextUtils.format("&6Ultra Ravager"));
                    ultraRavager.setCustomNameVisible(true);
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                    plugin.getNmsAccessor().setMaxHealth(ultraRavager, 500.0D, true);
                }
            }

            if (entity instanceof Chicken) {

                if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                    entity.remove();
                    plugin.getNmsHandler().spawnNMSEntity("Ravager", EntityType.RAVAGER, entity.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                    return;
                }

                if (plugin.getDay() >= 50) {
                    entity.getLocation().getWorld().spawnEntity(entity.getLocation(), EntityType.SILVERFISH);
                    entity.remove();
                }
            }
        }

        if (plugin.getDay() >= 60) {
            if (entity.getType() == EntityType.VILLAGER) {

                if (random.nextBoolean()) {
                    entity.getWorld().spawn(entity.getLocation(), Vex.class);
                } else {
                    entity.getWorld().spawn(entity.getLocation(), Vindicator.class);
                }
                entity.remove();
            }
        }
    }

    private void addMobEffects(LivingEntity entity, int force) {

        if (plugin.getDay() < 10) return;

        ArrayList<String> effectList = new ArrayList<>();

        effectList.add("SPEED;2");
        effectList.add("REGENERATION;3");
        effectList.add("INCREASE_DAMAGE;3");
        effectList.add("INVISIBILITY;0");
        effectList.add("JUMP;4");
        effectList.add("SLOW_FALLING;0");
        effectList.add("DAMAGE_RESISTANCE;2");
        if (plugin.getDay() < 50) effectList.add("GLOWING;0");

        int times = force == 100 ? plugin.getDay() < 25 ? random.nextInt(plugin.getDay() < 20 ? 3 : 4) + 1 : 5 : force;

        for (int i = 0; i < times; i++) {

            String[] s = effectList.get(random.nextInt(effectList.size())).split(";");

            PotionEffectType type = PotionEffectType.getByName(s[0]);
            int lvl = Integer.parseInt(s[1]);

            if (entity.hasPotionEffect(type)) {
                i--;
                return;
            }

            entity.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, lvl));
        }

        if (Main.DEBUG) {
            Bukkit.broadcastMessage("Efectos: " + times);

            for (PotionEffect p : entity.getActivePotionEffects()) {
                Bukkit.broadcastMessage(p.getType().getName());
            }
        }
    }

    private void spawnSkeletonClass(LivingEntity liv, Location l) {

        int bound = (plugin.getDay() < 60 ? 5 : 7) + 1;
        int randomClass = random.nextInt(bound);

        LivingEntity spider = null;
        Monster skeleton = null;
        World w = l.getWorld();


        if (liv instanceof CaveSpider) return;

        if (liv instanceof Spider) {
            if (randomClass == 5 || randomClass == 2) {
                skeleton = w.spawn(l, WitherSkeleton.class);
            } else {
                skeleton = w.spawn(l, Skeleton.class);
            }
            spider = liv;
        } else if (liv instanceof Skeleton) {
            skeleton = (Skeleton) liv;

            if (randomClass == 5 || randomClass == 2) {
                skeleton.remove();
                skeleton = w.spawn(l, WitherSkeleton.class);
            }
        }

        EntityEquipment eq = skeleton.getEquipment();

        ItemStack helmet = null;
        ItemStack chestplate = null;
        ItemStack legs = null;
        ItemStack boots = null;

        ItemStack mainHand = null;
        ItemStack offHand = null;
        Float mainDrop = 0.0f;
        Float offDrop = 0.0f;
        Float armorDropChance = 0.8F;

        Enchantment armorEnchant = null;
        int armorEnchantLvl = 0;

        Double health = 20.0D;
        String name = "";
        String id = "";

        if (plugin.getDay() >= 30) offHand = getPotionItemStack();

        if (plugin.getDay() >= 60 && random.nextInt(101) == 1) {

            if (skeleton != null) {
                skeleton.remove();
            }

            skeleton = w.spawn(l, WitherSkeleton.class);
            skeleton.getEquipment().setItemInMainHand(buildItem(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 32765).build());
            skeleton.getEquipment().setItemInMainHandDropChance(0.0f);

            skeleton.setRemoveWhenFarAway(false);
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            id = "skeleton_definitivo";
            name = "&6Ultra Esqueleto Definitivo";
            plugin.getNmsAccessor().setMaxHealth(skeleton, 400.0D, true);
        } else {

            if (randomClass == 1) {
                helmet = buildItem(Material.DIAMOND_HELMET).build();
                chestplate = buildItem(Material.DIAMOND_CHESTPLATE).build();
                legs = buildItem(Material.DIAMOND_LEGGINGS).build();
                boots = buildItem(Material.DIAMOND_BOOTS).build();
                mainHand = buildItem(Material.BOW).build();

                health = (plugin.getDay() < 30 ? 20.0D : plugin.getDay() < 50 ? 40.0D : 100.0D);

                if (plugin.getDay() >= 30) {
                    armorEnchant = Enchantment.PROTECTION_ENVIRONMENTAL;
                    armorEnchantLvl = plugin.getDay() < 60 ? 4 : 5;
                    if (plugin.getDay() >= 60) armorDropChance = 0.0f;
                }
            } else if (randomClass == 2) {
                helmet = buildItem(Material.CHAINMAIL_HELMET).build();
                chestplate = buildItem(Material.CHAINMAIL_CHESTPLATE).build();
                legs = buildItem(Material.CHAINMAIL_LEGGINGS).build();
                boots = buildItem(Material.CHAINMAIL_BOOTS).build();

                int punch_level = (plugin.getDay() < 30 ? 20 : plugin.getDay() < 50 ? 30 : 50);
                int actual_tactical_power_level = (plugin.getDay() < 50 ? 25 : plugin.getDay() < 60 ? 40 : 110); // Día 30

                health = (plugin.getDay() < 60 ? 40.0D : 60.0D);
                mainHand = buildItem(Material.BOW).addEnchant(Enchantment.ARROW_KNOCKBACK, punch_level).build();

                if (plugin.getDay() >= 30) {
                    mainHand = new ItemBuilder(mainHand).addEnchant(Enchantment.ARROW_DAMAGE, actual_tactical_power_level).build();
                }
            } else if (randomClass == 3) {
                helmet = buildItem(Material.IRON_HELMET).build();
                chestplate = buildItem(Material.IRON_CHESTPLATE).build();
                legs = buildItem(Material.IRON_LEGGINGS).build();
                boots = buildItem(Material.IRON_BOOTS).build();

                int fire_aspect_level = (plugin.getDay() < 30 ? 2 : plugin.getDay() < 50 ? 10 : 20);
                int infernal_skeleton_axe_sharpness = plugin.getDay() < 60 ? 25 : 100;
                Material material = (plugin.getDay() < 30 ? Material.IRON_AXE : Material.DIAMOND_AXE);

                mainHand = buildItem(material).addEnchant(Enchantment.FIRE_ASPECT, fire_aspect_level).build();
                health = (plugin.getDay() < 30 ? 20.0D : plugin.getDay() < 60 ? 40.0D : 100.0D);

                if (plugin.getDay() >= 50) {
                    mainHand = new ItemBuilder(mainHand).addEnchant(Enchantment.DAMAGE_ALL, infernal_skeleton_axe_sharpness).build();
                }
            } else if (randomClass == 4) {
                helmet = buildItem(Material.GOLDEN_HELMET).build();
                chestplate = buildItem(Material.GOLDEN_CHESTPLATE).build();
                legs = buildItem(Material.GOLDEN_LEGGINGS).build();
                boots = buildItem(Material.GOLDEN_BOOTS).build();

                int crossbow_sharp_level = (plugin.getDay() < 30 ? 20 : plugin.getDay() < 50 ? 25 : plugin.getDay() < 60 ? 50 : 100);

                mainHand = buildItem(Material.CROSSBOW).addEnchant(Enchantment.DAMAGE_ALL, crossbow_sharp_level).build();
                health = plugin.getDay() < 60 ? 40.0D : 60.0D;

                if (plugin.getDay() >= 30) {
                    skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (plugin.getDay() < 60 ? 1 : 3)));
                }
            } else if (randomClass == 5) {
                helmet = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1).setColor(Color.fromRGB(0xFF0000)).setUnbrekeable(true).build();
                chestplate = new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.fromRGB(0xFF0000)).setUnbrekeable(true).build();
                legs = new LeatherArmorBuilder(Material.LEATHER_LEGGINGS, 1).setColor(Color.fromRGB(0xFF0000)).setUnbrekeable(true).build();
                boots = new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.fromRGB(0xFF0000)).setUnbrekeable(true).build();
                armorDropChance = 0.0f;

                int bow_power_level = (plugin.getDay() < 30 ? 10 : plugin.getDay() < 50 ? 50 : plugin.getDay() < 60 ? 60 : 150);

                mainHand = buildItem(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, bow_power_level).build();
                health = plugin.getDay() < 60 ? 40.0D : 60.0D;
            } else if (randomClass == 6) {
                helmet = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1).setColor(Color.BLUE).setUnbrekeable(true).build();
                chestplate = new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.BLUE).setUnbrekeable(true).build();
                legs = new LeatherArmorBuilder(Material.LEATHER_LEGGINGS, 1).setColor(Color.BLUE).setUnbrekeable(true).build();
                boots = new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.BLUE).setUnbrekeable(true).build();
                mainHand = buildItem(Material.BOW).build();

                armorDropChance = 0.0f;
                name = TextUtils.format("&6Ultra Esqueleto Demoníaco");
                id = "demon_skeleton";

                health = 100.0D;

            } else if (randomClass == 7) {
                helmet = new LeatherArmorBuilder(Material.LEATHER_HELMET, 1).setColor(Color.GREEN).setUnbrekeable(true).build();
                chestplate = new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.GREEN).setUnbrekeable(true).build();
                legs = new LeatherArmorBuilder(Material.LEATHER_LEGGINGS, 1).setColor(Color.GREEN).setUnbrekeable(true).build();
                boots = new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.GREEN).setUnbrekeable(true).build();
                mainHand = buildItem(Material.BOW).build();
                offHand = getPotionItemStack2();
                armorDropChance = 0.0f;
                health = 100.0D;
                name = "&6Ultra Esqueleto Científico";
            } else {
                helmet = buildItem(Material.DIAMOND_HELMET).build();
                chestplate = buildItem(Material.DIAMOND_CHESTPLATE).build();
                legs = buildItem(Material.DIAMOND_LEGGINGS).build();
                boots = buildItem(Material.DIAMOND_BOOTS).build();
                mainHand = buildItem(Material.BOW).build();

                health = (plugin.getDay() < 30 ? 20.0D : plugin.getDay() < 50 ? 40.0D : 100.0D);

                if (plugin.getDay() >= 30) {
                    armorEnchant = Enchantment.PROTECTION_ENVIRONMENTAL;
                    armorEnchantLvl = plugin.getDay() < 60 ? 4 : 5;
                    if (plugin.getDay() >= 60) armorDropChance = 0.0f;
                }
            }
        }

        if (armorEnchant != null) {
            helmet = new ItemBuilder(helmet).addEnchant(armorEnchant, armorEnchantLvl).build();
            chestplate = new ItemBuilder(chestplate).addEnchant(armorEnchant, armorEnchantLvl).build();
            legs = new ItemBuilder(legs).addEnchant(armorEnchant, armorEnchantLvl).build();
            boots = new ItemBuilder(boots).addEnchant(armorEnchant, armorEnchantLvl).build();
        }

        if (helmet != null) eq.setHelmet(helmet);
        if (chestplate != null) eq.setChestplate(chestplate);
        if (legs != null) eq.setLeggings(legs);
        if (boots != null) eq.setBoots(boots);

        if (mainHand != null) eq.setItemInMainHand(mainHand);
        if (offHand != null) eq.setItemInOffHand(offHand);
        if (!name.isEmpty()) skeleton.setCustomName(TextUtils.format(name));
        if (!id.isEmpty())
            skeleton.getPersistentDataContainer().set(new NamespacedKey(plugin, id), PersistentDataType.BYTE, (byte) 1);
        plugin.getNmsAccessor().setMaxHealth(skeleton, health, true);
        skeleton.setHealth(health);

        eq.setItemInMainHandDropChance(mainDrop);
        eq.setItemInOffHandDropChance(offDrop);

        eq.setHelmetDropChance(armorDropChance);
        eq.setChestplateDropChance(armorDropChance);
        eq.setLeggingsDropChance(armorDropChance);
        eq.setBootsDropChance(armorDropChance);

        if (spider != null) {
            spider.addPassenger(skeleton);
        }
    }

    private void spawnNetheriteMob(CreatureSpawnEvent event) {

        if (plugin.getDay() < 25) return;

        if (event.getEntityType() == EntityType.SLIME) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
                return;
            }

            Slime slime = (Slime) event.getEntity();
            Double health = (plugin.getDay() < 50 ? slime.getHealth() * 2 : slime.getHealth() * 4);

            slime.setSize((plugin.getDay() < 50) ? 15 : 16);
            plugin.getNmsAccessor().setMaxHealth(slime, health, true);
            slime.setCustomName(ChatColor.GOLD + "GIGA Slime");
        }

        if (event.getEntityType() == EntityType.MAGMA_CUBE) {

            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
                return;
            }
            int size = (plugin.getDay() < 50 ? 16 : 17);

            // Límite de 10 Giga MagmaCubes
            if (event.getLocation().getWorld().getEntitiesByClass(MagmaCube.class).stream().filter(entity -> entity.getSize() == size).collect(Collectors.toList()).size() >= 10) {
                event.setCancelled(true);
                return;
            }


            MagmaCube magmaCube = (MagmaCube) event.getEntity();

            magmaCube.setSize(size);
            magmaCube.setCustomName(ChatColor.GOLD + "GIGA MagmaCube");

            if (plugin.getDay() >= 50) {
                plugin.getNmsAccessor().setMaxHealth(magmaCube, plugin.getNmsAccessor().getMaxHealth(magmaCube) * 2, true);
            }
        }

        if (event.getEntityType() == EntityType.GHAST) {
            if (event.getLocation().getWorld().getEnvironment() != World.Environment.THE_END && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {

                if (plugin.getDay() < 40) {
                    Double health = ThreadLocalRandom.current().nextDouble(40, 60 + 1);

                    LivingEntity GhastDemon = event.getEntity();
                    plugin.getNmsAccessor().setMaxHealth(GhastDemon, health, true);
                    GhastDemon.setHealth(health);
                    GhastDemon.setCustomName(ChatColor.GOLD + "Ghast Demoníaco");
                } else {

                    int r = random.nextInt(100) + 1;

                    Double HPGenerator = ThreadLocalRandom.current().nextDouble(40, 60 + 1);
                    LivingEntity GhastDemon = event.getEntity();
                    plugin.getNmsAccessor().setMaxHealth(GhastDemon, HPGenerator, true);
                    GhastDemon.setHealth(HPGenerator);

                    if (r <= 75) {
                        GhastDemon.setCustomName(ChatColor.GOLD + "Demonio flotante");
                        GhastDemon.getPersistentDataContainer().set(new NamespacedKey(plugin, "demonio_flotante"), PersistentDataType.BYTE, (byte) 1);
                    } else {
                        GhastDemon.setCustomName(ChatColor.GOLD + "Ghast Demoníaco");
                        GhastDemon.getPersistentDataContainer().set(new NamespacedKey(plugin, "ghast_demoniaco"), PersistentDataType.BYTE, (byte) 1);
                    }
                }
            }
        }
    }

    public void explodeCat(LivingEntity cat) {
        if (this.gatosSupernova.contains(cat)) return;
        this.gatosSupernova.add(cat);

        boolean canContinue = true;

        if (Bukkit.getOnlinePlayers().size() == 0) canContinue = false;
        if (gatosSupernova.size() > 2) canContinue = false;

        if (!canContinue) {
            cat.remove();
            return;
        }

        final World w = cat.getWorld();
        final Location loc = cat.getLocation().clone();
        final Chunk chunk = loc.getChunk();

        if (!chunk.isForceLoaded()) chunk.setForceLoaded(true);
        if (!chunk.isLoaded()) chunk.load();

        Bukkit.broadcastMessage(TextUtils.format("&cUn gato supernova va a explotar en: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " (" + cat.getWorld().getName() + ")."));
        Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (cat == null) {
                    if (chunk.isForceLoaded()) chunk.setForceLoaded(false);
                    if (chunk.isLoaded()) chunk.unload();
                    return;
                }
                if (!gatosSupernova.contains(cat)) {
                    if (chunk.isForceLoaded()) chunk.setForceLoaded(false);
                    if (chunk.isLoaded()) chunk.unload();
                    return;
                }

                float power = Float.valueOf(plugin.getConfig().getInt("Toggles.Gatos-Supernova.Explosion-Power"));
                boolean breakBlocks = plugin.getConfig().getBoolean("Toggles.Gatos-Supernova.Destruir-Bloques");
                boolean placeFire = plugin.getConfig().getBoolean("Toggles.Gatos-Supernova.Fuego");

                w.createExplosion(loc, power, placeFire, breakBlocks, cat);
                gatosSupernova.remove(cat);
                cat.remove();

                if (chunk.isForceLoaded()) chunk.setForceLoaded(false);
                if (chunk.isLoaded()) chunk.unload();
            }
        }, 20 * 30);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

        if (plugin.getDay() >= 20) {
            LivingEntity mob = event.getEntity();

            if (plugin.getDay() < 40) {
                if (mob instanceof IronGolem || mob.getType() == PIGMAN || mob instanceof Ghast || mob instanceof Guardian || mob instanceof Enderman || mob instanceof Witch || mob instanceof WitherSkeleton || mob instanceof Evoker || mob instanceof Phantom || mob instanceof Slime || mob instanceof Drowned || mob instanceof Blaze) {
                    event.getDrops().clear();
                }

                if (event.getEntity().getKiller() == null) return;
                Player killer = event.getEntity().getKiller();
                if (mob instanceof Ravager) {

                    int prob = random.nextInt(100) + 1;
                    int needed = 1;

                    if (plugin.getDay() >= 25) {

                        needed = 20;
                    }

                    int randomSentence = ThreadLocalRandom.current().nextInt(1, 4 + 1);

                    if (prob <= needed) {

                        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING, 1, (short) 3);
                        mob.getWorld().dropItem(mob.getLocation(), totem);

                        killer.sendMessage(ChatColor.YELLOW + "¡Un tótem!");
                        killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, -5);

                    } else if (randomSentence == 1) {
                        assert killer != null;
                        killer.sendMessage(ChatColor.RED + "Vaya que mala suerte, ese ravager no tenia nada :(");
                    } else if (randomSentence == 2) {
                        assert killer != null;
                        killer.sendMessage(ChatColor.RED + "¡Porras... otro ravager sin suerte!");
                    } else if (randomSentence == 3) {
                        assert killer != null;
                        killer.sendMessage(ChatColor.RED + "Nada... hoy no hay totem :(");
                    } else if (randomSentence == 4) {
                        assert killer != null;
                        killer.sendMessage(ChatColor.RED + "¡Hoy no es tu día!");
                    }
                }
            } else {

                if (mob instanceof IronGolem || mob instanceof Ghast || mob instanceof Guardian || mob instanceof Enderman || mob instanceof Witch || mob instanceof WitherSkeleton || mob instanceof Evoker || mob instanceof Phantom || mob instanceof Slime || mob instanceof Drowned || mob instanceof Blaze) {
                    event.getDrops().clear();
                }

                if (event.getEntityType() == EntityType.CAT || event.getEntityType() == EntityType.OCELOT) {

                    if (gatosSupernova.contains(event.getEntity())) gatosSupernova.remove(event.getEntity());
                    if (event.getEntity().getCustomName() == null) return;
                    if (event.getEntity().getCustomName().contains(TextUtils.format("&6Gato Gal"))) {

                        Location l = event.getEntity().getLocation();
                        int x = (int) l.getX();
                        int y = (int) l.getY();
                        int z = (int) l.getZ();

                        Bukkit.broadcastMessage(TextUtils.format("&cLa maldición de un Gato Galáctico ha comenzado en: " + x + ", " + y + ", " + z));
                        new GatoGalacticoTask(event.getEntity().getLocation(), plugin).runTaskTimer(plugin, 0, 20L);
                    }
                }

                if (mob.getType() == PIGMAN) {
                    if (mob.getCustomName() == null) {
                        event.getDrops().clear();
                        return;
                    }

                    if (mob.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Carlos el Esclavo")) {
                        int r = random.nextInt(100) + 1;
                        int chance = plugin.getDay() < 60 ? 100 : 33;

                        if (r <= chance) event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 32));
                    }
                }

                if (mob instanceof Villager) {
                    if (mob.getCustomName() == null) return;
                    if (mob.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Jess la Emperatriz")) {
                        int r = random.nextInt(100) + 1;
                        int prob = 100;
                        if (plugin.getDay() >= 60) {
                            prob = 33;
                        }
                        if (r <= prob) {
                            event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 2));
                        }
                    }
                }

                if (event.getEntity() instanceof Ravager) {
                    Ravager ravager = (Ravager) event.getEntity();
                    if (ravager.getCustomName() == null) return;
                    if (ravager.getCustomName().contains(TextUtils.format("&6Ultra Ravager"))) {
                        if (ravager.getWorld().getEnvironment() != World.Environment.NETHER) {
                            event.getDrops().clear();
                        } else {
                            if ((random.nextInt(100) + 1) <= (plugin.getDay() < 60 ? 100 : 33)) {
                                event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
                            }
                        }
                    }
                }
            }

            if (plugin.getDay() < 60 && plugin.getDay() >= 50) {
                if (event.getEntity().getType() == EntityType.GIANT) {
                    event.getDrops().add(new ItemBuilder(Material.BOW).setDisplayName(TextUtils.format("&bArco de Gigante")).addEnchant(Enchantment.ARROW_DAMAGE, 10).build());
                }

                if (event.getEntity().getType() == EntityType.WITHER_SKELETON) {
                    if (event.getEntity().getCustomName() == null) return;
                    if (event.getEntity().getCustomName().contains(TextUtils.format("&6Wither Skeleton Emperador"))) {

                        if (plugin.getDay() < 60) {
                            int prob = (int) (Math.random() * 100) + 1;
                            if (prob <= 50) {
                                event.getDrops().add(PermadeathItems.craftNetheriteSword());
                            }
                        }
                    }
                }
            }
        }

        this.runNetheriteCheck(event);
    }

    private void runNetheriteCheck(EntityDeathEvent event) {
        if (plugin.getDay() < 25 || plugin.getDay() >= 30 || event.getEntity().getKiller() == null) return;

        LivingEntity Mob = event.getEntity();

        int hp = Integer.parseInt(Objects.requireNonNull(Main.instance.getConfig().getString("Toggles.Netherite.Helmet")));
        int cp = Integer.parseInt(Objects.requireNonNull(Main.instance.getConfig().getString("Toggles.Netherite.Chestplate")));
        int lp = Integer.parseInt(Objects.requireNonNull(Main.instance.getConfig().getString("Toggles.Netherite.Leggings")));
        int bp = Integer.parseInt(Objects.requireNonNull(Main.instance.getConfig().getString("Toggles.Netherite.Boots")));

        int RandProb = ThreadLocalRandom.current().nextInt(1, 101);

        if (Mob instanceof CaveSpider && RandProb <= hp) {
            event.getDrops().clear();
            event.getDrops().add(NetheriteArmor.craftNetheriteHelmet());
        }

        if (Mob instanceof Slime && RandProb <= cp) {
            event.getDrops().clear();
            event.getDrops().add(NetheriteArmor.craftNetheriteChest());
        }

        if (Mob instanceof MagmaCube && RandProb <= lp) {
            event.getDrops().clear();
            event.getDrops().add(NetheriteArmor.craftNetheriteLegs());
        }

        if (Mob instanceof Ghast && RandProb <= bp) {
            event.getDrops().clear();
            event.getDrops().add(NetheriteArmor.craftNetheriteBoots());
        }
    }

    private void spawnUltraRavager(CreatureSpawnEvent event) {

        Ravager ravager = event.getLocation().getWorld().spawn(event.getLocation(), Ravager.class);
        LivingEntity carlos = (LivingEntity) event.getLocation().getWorld().spawnEntity(event.getLocation(), PIGMAN);
        Villager jess = event.getLocation().getWorld().spawn(event.getLocation(), Villager.class);

        carlos.addPassenger(jess);
        ravager.addPassenger(carlos);

        plugin.getNmsAccessor().setMaxHealth(jess, 500.0D, true);

        plugin.getNmsAccessor().setMaxHealth(carlos, 150.0D, true);

        plugin.getNmsAccessor().setMaxHealth(ravager, 240.0D, true);

        jess.setCustomName(ChatColor.GREEN + "Jess la Emperatriz");
        carlos.setCustomName(ChatColor.GREEN + "Carlos el Esclavo");
        ravager.setCustomName(ChatColor.GREEN + "Ultra Ravager");

        jess.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_APPLE, 2));
        jess.getEquipment().setItemInMainHandDropChance(0);

        carlos.getEquipment().setItemInMainHand(new ItemStack(Material.GOLD_INGOT, 32));
        carlos.getEquipment().setItemInMainHandDropChance(0);

        ravager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        ravager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        ravager.getPersistentDataContainer().set(new NamespacedKey(plugin, "ultra_ravager"), PersistentDataType.BYTE, (byte) 1);

        jess.setRemoveWhenFarAway(true);
        ravager.setRemoveWhenFarAway(true);
        carlos.setRemoveWhenFarAway(true);

        event.setCancelled(true);
    }

    private void spawnBeginningMob(CreatureSpawnEvent e) {

        World beginningWorld = e.getLocation().getWorld();
        Location location = e.getLocation();

        if (plugin.getDay() < 0) return;
        if (plugin.getBeginningManager() == null) return;
        if (plugin.getBeginningManager().getBeginningWorld() == null) return;
        if (!beginningWorld.getName().equalsIgnoreCase(plugin.getBeginningManager().getBeginningWorld().getName()))
            return;

        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
            if (beginningWorld.getLivingEntities().size() > 70) {
                return;
            }

            int p = random.nextInt(101);

            if (p <= 60) {
                WitherSkeleton skeleton = (WitherSkeleton) plugin.getNmsHandler().spawnNMSEntity("SkeletonWither", EntityType.WITHER_SKELETON, e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);

                skeleton.getEquipment().setChestplate(new LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, 1).setColor(Color.fromRGB(255, 182, 193)).build());
                skeleton.getEquipment().setBoots(new LeatherArmorBuilder(Material.LEATHER_BOOTS, 1).setColor(Color.fromRGB(255, 182, 193)).build());

                int enchantLevel = (int) (Math.random() * 5) + 1;
                skeleton.getEquipment().setItemInMainHand(new ItemBuilder(PermadeathItems.craftNetheriteSword()).addEnchant(Enchantment.DAMAGE_ALL, enchantLevel).build());

                skeleton.getEquipment().setChestplateDropChance(0);
                skeleton.getEquipment().setBootsDropChance(0);
                skeleton.getEquipment().setItemInMainHandDropChance(0);

                skeleton.setCustomName(TextUtils.format("&6Wither Skeleton Rosáceo"));
                skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                plugin.getNmsAccessor().setMaxHealth(skeleton, 100.0D, true);
            }

            if (p > 60 && p <= 75) {
                Vex vex = beginningWorld.spawn(location, Vex.class);
                vex.getEquipment().setHelmet(new ItemBuilder(Material.valueOf("HONEY_BLOCK")).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                vex.getEquipment().setItemInMainHand(new ItemBuilder(Material.END_CRYSTAL).addEnchant(Enchantment.DAMAGE_ALL, 15).addEnchant(Enchantment.KNOCKBACK, 10).build());
                vex.getEquipment().setHelmetDropChance(0);
                vex.getEquipment().setItemInMainHandDropChance(0);

                vex.setCustomName(TextUtils.format("&6Vex Definitivo"));
            }

            if (p > 75 && p <= 79) {
                Ghast ghast = (Ghast) plugin.getNmsHandler().spawnCustomGhast(e.getLocation().add(0, 5, 0), CreatureSpawnEvent.SpawnReason.CUSTOM, true);
                plugin.getNmsAccessor().setMaxHealth(ghast, 150.0D, true);
                ghast.setCustomName(TextUtils.format("&6Ender Ghast Definitivo"));
            }

            if (p >= 80) {
                Creeper c = plugin.getFactory().spawnEnderQuantumCreeper(e.getLocation(), null);
                plugin.getNmsAccessor().setMaxHealth(c, 100.0D, true);
                c.setExplosionRadius(7);
            }
        }
    }

    public ItemStack getPotionItemStack() {
        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
        arrow.setItemMeta(meta);
        return arrow;
    }

    public ItemStack getPotionItemStack2() {

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 60 * 20, 2), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3 * 60 * 20, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.GLOWING, 3 * 60 * 20, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 3 * 60 * 20, 2), false);
        arrow.setItemMeta(meta);
        return arrow;
    }

    private ItemBuilder buildItem(Material mat) {
        return new ItemBuilder(mat);
    }
}
