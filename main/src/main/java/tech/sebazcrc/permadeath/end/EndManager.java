package tech.sebazcrc.permadeath.end;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.end.demon.DemonPhase;
import tech.sebazcrc.permadeath.task.EndTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

import static tech.sebazcrc.permadeath.Main.instance;

public class EndManager implements Listener {

    private Main main;

    private List<Entity> enderCreepers;
    private List<Entity> enderGhasts;

    private ArrayList<Location> alreadyExploded = new ArrayList<>();

    private ArrayList<Enderman> invulnerable = new ArrayList<>();

    private SplittableRandom random;

    public EndManager(Main main) {
        this.main = main;

        main.getServer().getPluginManager().registerEvents(this, main);

        this.enderCreepers = new ArrayList<>();
        this.enderGhasts = new ArrayList<>();
        this.random = new SplittableRandom();
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {

        if (isInEnd(e.getEntity().getLocation())) {

            if (e.getEntity() instanceof TNTPrimed) {

                if (!(e.getEntity() instanceof TNTPrimed)) return;
                if (e.getEntity().getCustomName() == null) return;
                if (!e.getEntity().getCustomName().equalsIgnoreCase("dragontnt")) return;

                if (instance.getConfig().getBoolean("Toggles.End.Optimizar-TNT")) {
                    e.setRadius(5.0F);
                } else {
                    e.setRadius(15.0F);
                }
            }
        }
    }

    @EventHandler
    public void onEffectApply(AreaEffectCloudApplyEvent e) {

        AreaEffectCloud area = e.getEntity();

        if (isInEnd(area.getLocation())) {
            if (area.getParticle() == Particle.VILLAGER_HAPPY) {
                for (Entity all : e.getAffectedEntities()) {
                    if (all instanceof Player) {
                        e.setCancelled(true);
                    } else if (all.getType() == EntityType.ENDERMAN) {
                        final Enderman man = (Enderman) all;
                        invulnerable.add(man);
                        Bukkit.getServer().getScheduler().runTaskLater(main, new Runnable() {
                            @Override
                            public void run() {
                                if (man == null) return;
                                invulnerable.remove(man);
                            }
                        }, 20 * 15);
                        e.setCancelled(true);
                    }
                }
            }

            if (area.getParticle() == Particle.SMOKE_NORMAL) {
                for (Entity all : e.getAffectedEntities()) {
                    if (all instanceof Player) {
                        Player p = (Player) all;
                        if (p.getLocation().distance(area.getLocation()) <= 3.0D) {
                            if (p.getActivePotionEffects().size() >= 1) {
                                for (PotionEffect effect : p.getActivePotionEffects()) {
                                    p.removePotionEffect(effect.getType());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageBE(EntityDamageByEntityEvent e) {

        if (e.getEntity() instanceof Enderman) {
            Enderman man = (Enderman) e.getEntity();
            if (invulnerable.contains(man)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEMDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Enderman) {
            Enderman man = (Enderman) e.getEntity();
            if (invulnerable.contains(man)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDead(EntityDeathEvent e) {

        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) {
            if (main.getTask() != null) {

                main.getTask().setDied(true);
                for (Player all : main.endWorld.getPlayers()) {
                    spawnFireworks(all.getLocation().add(0, 1, 0), 1);
                }
            }
        }

        Entity entity = e.getEntity();

        if (enderGhasts.contains(entity)) {
            enderGhasts.remove(entity);
            e.getDrops().clear();
            e.setDroppedExp(0);
        }

        if (enderCreepers.contains(entity)) {
            enderCreepers.remove(entity);
            e.getDrops().clear();
            e.setDroppedExp(0);
        }

        if (entity instanceof Shulker && ((Shulker) entity).getColor() != DyeColor.RED) {
            boolean isSure = true;
            for (Entity near : e.getEntity().getNearbyEntities(2, 2, 2)) {
                if (near.getType() == EntityType.PRIMED_TNT) {
                    isSure = false;
                }
            }
            if (isSure) {
                TNTPrimed tnt = (TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(80);

                tnt.setCustomName("tntdeath");
                tnt.setCustomNameVisible(false);
                e.getDrops().clear();

                int randomProb = new Random().nextInt(99);
                randomProb = randomProb + 1;

                if (main.getDay() <= 39) {

                    if (randomProb <= 20) {

                        if (instance.getShulkerEvent().isRunning()) {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
                        } else {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 1));
                        }
                    }
                } else if (main.getDay() >= 40) {

                    if (randomProb <= 2) {

                        if (instance.getShulkerEvent().isRunning()) {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
                        } else {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 1));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent e) {

        Entity t = e.getEntity();

        if (isInEnd(t.getLocation())) {

            if (e.getEntity().getType() == EntityType.ENDER_CRYSTAL && main.getTask() != null) {

                if (alreadyExploded.contains(e.getLocation())) return;

                final EnderCrystal c = (EnderCrystal) e.getEntity();

                if (c.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                    int random = new Random().nextInt(main.getEndData().getTimeList().size());
                    main.getTask().getRegenTime().put(c.getLocation(), main.getEndData().getTimeList().get(random));

                    Location nL = e.getLocation().clone().add(0, 10, 0);
                    Entity g = instance.getNmsHandler().spawnCustomGhast(nL, CreatureSpawnEvent.SpawnReason.CUSTOM, true);
                    final Location loc = e.getLocation();

                    enderGhasts.add(g);
                    alreadyExploded.add(loc);

                    Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                        @Override
                        public void run() {
                            if (alreadyExploded.contains(loc)) {
                                alreadyExploded.remove(loc);
                            }
                        }
                    }, 20 * 5);

                    for (Player all : main.endWorld.getPlayers()) {
                        all.playSound(nL, Sound.ENTITY_WITHER_SPAWN, 100.0F, 100.0F);
                    }
                }
            }
        }

        if (e.getEntity() instanceof TNTPrimed) {
            if (e.getEntity().getCustomName() == null) return;
            if (!e.getEntity().getCustomName().equalsIgnoreCase("dragontnt")) return;


            if (!e.blockList().isEmpty()) {

                Location egg = new Location(main.endWorld, 0, 0, 0);
                Location withY = main.endWorld.getHighestBlockAt(egg).getLocation();

                if (e.getLocation().distance(withY) <= instance.getConfig().getInt("Toggles.End.Protect-Radius") && instance.getConfig().getBoolean("Toggles.End.Protect-End-Spawn")) {

                    e.blockList().clear();
                    e.setYield(0);
                    return;
                }

                List<FallingBlock> fallingBlocks = new ArrayList<>();
                List<Block> blockList = new ArrayList<>(e.blockList());
                for (Block b : blockList) {
                    float x = (float) (-0.2 + (float) (Math.random() * ((0.2 - -0.2) + 0.2)));
                    float y = -1 + (float) (Math.random() * ((1 - -1) + 1));
                    float z = (float) (-0.2 + (float) (Math.random() * ((0.2 - -0.2) + 0.2)));

                    if (b.getType() == Material.END_STONE || b.getType() == Material.END_STONE_BRICKS) {

                        FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getState().getData());
                        b.getState().setData(b.getState().getData());
                        fb.setVelocity(new Vector(x, y, z));
                        fb.setDropItem(false);
                        fb.setMetadata("Exploded", new FixedMetadataValue(main, 0));
                        fallingBlocks.add(fb);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Block b : blockList) {
                                    b.getState().update();
                                    this.cancel();
                                }
                            }
                        }.runTaskLater(main, 2L);
                        e.blockList().clear();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDragonRegen(EntityRegainHealthEvent e) {

        if (e.getEntity() instanceof EnderDragon) {

            e.setAmount(e.getAmount() / 2);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        if (e.isCancelled()) return;
        LivingEntity entity = e.getEntity();
        if (isInEnd(entity.getLocation())) {
            if (main.getTask() == null) {
                for (Entity n : e.getLocation().getWorld().getEntitiesByClass(EnderDragon.class)) {
                    if (n.isValid() && !n.isDead()) {
                        n.setCustomName(TextUtils.format(instance.getConfig().getString("Toggles.End.PermadeathDemon.DisplayName")));
                        ((LivingEntity) n).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(instance.getConfig().getInt("Toggles.End.PermadeathDemon.Health"));
                        ((LivingEntity) n).setHealth(instance.getConfig().getInt("Toggles.End.PermadeathDemon.Health"));
                        main.setTask(new EndTask(main, (EnderDragon) n));
                        main.getTask().runTaskTimer(main, 0, 20L);
                    }
                }
            } else {

                if (!main.getTask().isDied()) {
                    Entity n = main.getTask().getEnderDragon();
                    if (n.getType() == EntityType.ENDER_DRAGON && n.isValid() && !n.isDead()) {
                        EnderDragon dragon = (EnderDragon) n;
                        int enragedHealth = instance.getConfig().getInt("Toggles.End.PermadeathDemon.EnragedHealth");

                        if (enragedHealth > instance.getConfig().getInt("Toggles.End.PermadeathDemon.Health") || enragedHealth < 10) {
                            enragedHealth = instance.getConfig().getInt("Toggles.End.PermadeathDemon.Health") / 2;
                        }

                        if (dragon.getHealth() <= enragedHealth) {
                            main.getTask().setCurrentDemonPhase(DemonPhase.ENRAGED);
                        }
                    }
                }
            }
            if (!(entity instanceof Enderman)) return;

            int cCP = instance.getConfig().getInt("Toggles.End.Ender-Creeper-Count");
            if (cCP < 1 || cCP > 1000) {
                cCP = 20;
            }

            int cGP = instance.getConfig().getInt("Toggles.End.Ender-Ghast-Count");

            if (cGP < 1 || cGP > 1000) {
                cGP = 170;
            }

            int creeperProb = random.nextInt(cCP) + 1;
            int ghastProb = random.nextInt(cGP) + 1;

            if (creeperProb == 1) {
                if (instance.getDay() < 60) {
                    instance.getFactory().spawnEnderCreeper(e.getLocation(), null);
                } else {
                    instance.getFactory().spawnEnderQuantumCreeper(e.getLocation(), null);
                }
                e.setCancelled(true);
            }

            if (ghastProb == 1) {
                boolean dragonDied = true;
                if (main.endWorld.getEntitiesByClass(EnderDragon.class).size() >= 1) {
                    dragonDied = false;
                }
                if (dragonDied) {
                    main.getNmsHandler().spawnCustomGhast(e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM, true);
                    e.setCancelled(true);
                }
            }

            if (instance.getConfig().getBoolean("Toggles.Optimizar-Mob-Spawns")) {
                int removeProb = random.nextInt(100) + 1;
                if (removeProb <= 10) {
                    e.setCancelled(true);
                }
            }
        } else {
            if (e.getEntity() instanceof Enderman) {
                if (main.getDay() >= 40) {
                    Enderman man = (Enderman) e.getEntity();
                    if (random.nextInt(100) + 1 == 1) {
                        instance.getNmsAccessor().injectHostilePathfinders(man);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {

        if (!isInEnd(e.getEntity().getLocation())) return;

        if (e.getHitBlock() != null) {

            if (e.getEntity() instanceof ShulkerBullet) {

                ShulkerBullet b = (ShulkerBullet) e.getEntity();

                if (b.getShooter() instanceof Shulker) {

                    Shulker s = (Shulker) b.getShooter();

                    if (s.getLocation().distance(e.getHitBlock().getLocation()) >= 4.0) {

                        Location w = e.getHitBlock().getLocation();

                        if (e.getHitBlockFace() == BlockFace.EAST) {

                            w = e.getHitBlock().getRelative(BlockFace.EAST).getLocation();
                        }

                        if (e.getHitBlockFace() == BlockFace.UP) {

                            w = e.getHitBlock().getRelative(BlockFace.UP).getLocation();
                        }

                        if (e.getHitBlockFace() == BlockFace.DOWN) {

                            w = e.getHitBlock().getRelative(BlockFace.DOWN).getLocation();
                        }

                        if (e.getHitBlockFace() == BlockFace.NORTH) {

                            w = e.getHitBlock().getRelative(BlockFace.NORTH).getLocation().add(0, 1, 0);
                        }

                        if (e.getHitBlockFace() == BlockFace.SOUTH) {

                            w = e.getHitBlock().getRelative(BlockFace.SOUTH).getLocation().add(0, 1, 0);
                        }

                        w.getBlock().setType(Material.AIR);

                        TNTPrimed tnt = (TNTPrimed) s.getWorld().spawnEntity(w, EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(40);

                        tnt.setCustomName("tnt");
                        tnt.setCustomNameVisible(false);
                    }
                }
            }
        }

        if (e.getHitEntity() != null) {

            if (e.getEntity() instanceof ShulkerBullet) {

                ShulkerBullet b = (ShulkerBullet) e.getEntity();

                if (b.getShooter() instanceof Shulker) {

                    Shulker s = (Shulker) b.getShooter();

                    if (s.getLocation().getX() == e.getHitEntity().getLocation().getX() && s.getLocation().getY() == e.getHitEntity().getLocation().getY() && s.getLocation().getZ() == e.getHitEntity().getLocation().getZ()) {

                        return;
                    }

                    TNTPrimed tnt = (TNTPrimed) s.getWorld().spawnEntity(e.getHitEntity().getLocation(), EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(20);

                    tnt.setCustomName("tnt");
                    tnt.setCustomNameVisible(false);

                }
            }
        }
    }

    public static void spawnFireworks(Location location, int amount) {
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public boolean isInEnd(Location p) {

        return p.getWorld().getName().endsWith("_the_end");
    }
}
