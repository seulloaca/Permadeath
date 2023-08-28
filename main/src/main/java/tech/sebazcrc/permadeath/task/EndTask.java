package tech.sebazcrc.permadeath.task;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.end.demon.DemonCurrentAttack;
import tech.sebazcrc.permadeath.end.demon.DemonPhase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class EndTask extends BukkitRunnable {

    private Map<Location, Integer> regenTime = new HashMap<>();
    private Location teleportLocation;
    private DemonCurrentAttack currentAttack = DemonCurrentAttack.NONE;
    private DemonPhase currentDemonPhase = DemonPhase.NORMAL;
    private MovesTask currentMovesTask = null;

    private EnderDragon enderDragon;
    private Main main;

    private int timeForTnT = 30;
    private int nextDragonAttack = 20;
    private int lightingDuration = 5;
    private int nightVisionDuration = 5;
    private int timeForEnd360 = 20;

    private boolean nightVision = false;
    private boolean isDied;
    private boolean attack360 = false;
    private boolean lightingRain = false;
    private boolean canMakeAnAttack = true;
    private boolean decided = false;

    private Location eggLocation;

    private SplittableRandom random = new SplittableRandom();

    public EndTask(Main plugin, EnderDragon enderDragon) {
        this.main = plugin;

        this.isDied = false;
        this.enderDragon = enderDragon;

        int y = main.endWorld.getMaxHeight() - 1;
        while (y > 0 && main.endWorld.getBlockAt(0, y, 0).getType() != Material.BEDROCK) {
            y--;
        }
        this.eggLocation = main.endWorld.getHighestBlockAt(new Location(main.endWorld, 0, y, 0)).getLocation();

        enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Main.instance.getConfig().getInt("Toggles.End.PermadeathDemon.Health"));
        enderDragon.setHealth(Main.instance.getConfig().getInt("Toggles.End.PermadeathDemon.Health"));

        teleportLocation = eggLocation.clone().add(0, 2, 0);
        teleportLocation.setPitch(enderDragon.getLocation().getPitch());

        for (Entity all : enderDragon.getWorld().getEntitiesByClass(Ghast.class)) {
            all.remove();
        }
    }

    @Override
    public void run() {
        if (isDied || enderDragon.isDead()) {
            main.setTask(null);
            cancel();
            return;
        }
        tickTnTAttack();
        tickLightingRain();
        tickNightVision();
        tick360Attack();
        tickDemonPhase();
        tickRandomLighting();
        tickEnderCrystals();
        tickDragonAttacks();
    }

    private void tickEnderCrystals() {
        if (!regenTime.isEmpty()) {
            for (org.bukkit.Location loc : regenTime.keySet()) {
                int time = regenTime.get(loc);
                if (time >= 1) {
                    regenTime.replace(loc, time, time - 1);
                } else {
                    loc.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
                    regenTime.remove(loc);
                    if (loc.getWorld().getBlockAt(loc) != null) {
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.BEDROCK || loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            return;
                        }
                        loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void tickRandomLighting() {
        /**
         * Código previo poco optimizado
         int eFound = 0;
         ArrayList<Location> locations = new ArrayList<>();
         for (Entity e : main.endWorld.getEntities()) {
         if (e instanceof Enderman) {

         if (eFound < 4) {

         e.getWorld().strikeLightning(e.getLocation().add(5, 0, 0));
         eFound = eFound + 1;
         }
         }
         }
         */

        int x = (random.nextBoolean() ? 1 : -1) * random.nextInt(21);
        int z = (random.nextBoolean() ? 1 : -1) * random.nextInt(21);
        int y = main.endWorld.getHighestBlockYAt(x, z);

        if (y < 0) return;

        main.endWorld.strikeLightning(new Location(main.endWorld, x, y, z));
    }

    private void tickDemonPhase() {
        if (currentDemonPhase == DemonPhase.ENRAGED) {
            EnderDragon dragon = (EnderDragon) enderDragon;
            dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 7));
            dragon.setCustomName(TextUtils.format(main.getConfig().getString("Toggles.End.PermadeathDemon.DisplayNameEnraged")));
        } else {
            EnderDragon dragon = (EnderDragon) enderDragon;
            dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5));
        }
    }

    private void tick360Attack() {
        if (enderDragon.getLocation().distance(eggLocation) >= 10.0D && decided) {
            decided = false;
        }
        if (enderDragon.getLocation().distance(eggLocation) <= 3.0D && !decided) {

            decided = true;
            enderDragon.setRotation(enderDragon.getLocation().getPitch(), 0);

            if (random.nextInt(10) <= 7) {
                start360attack();
            }
        }

        if (attack360) {
            canMakeAnAttack = false;
            if (timeForEnd360 >= 1) {
                timeForEnd360 = timeForEnd360 - 1;
            }
            if (timeForEnd360 >= 16) {
                EnderDragon dragon = enderDragon;
                if (dragon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL) {
                    dragon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);
                }
                dragon.teleport(teleportLocation);
            }

            if (timeForEnd360 == 15) {
                this.currentMovesTask = new MovesTask(main, (EnderDragon) enderDragon, teleportLocation);
                currentMovesTask.runTaskTimer(main, 5L, 5L);
            }

            if (timeForEnd360 == 0) {
                if (currentMovesTask != null) {
                    currentMovesTask.cancel();
                    currentMovesTask = null;
                }

                canMakeAnAttack = true;
                timeForEnd360 = 20;
                attack360 = false;
                enderDragon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
            }
        }
    }

    private void tickDragonAttacks() {
        if (nextDragonAttack >= 1) {
            nextDragonAttack = nextDragonAttack - 1;
        } else if (nextDragonAttack == 0) {
            if (getCurrentDemonPhase() == DemonPhase.NORMAL) {

                nextDragonAttack = 60;
            } else {

                nextDragonAttack = 40;
            }

            if (canMakeAnAttack) {
                chooseAnAttack();
            } else {
                currentAttack = DemonCurrentAttack.NONE;
            }
            if (currentAttack == DemonCurrentAttack.NONE) {
                return;
            }
            if (currentAttack == DemonCurrentAttack.ENDERMAN_BUFF) {

                int endermanschoosed = 0;
                ArrayList<Enderman> endermen = new ArrayList<>();

                for (Enderman man : main.endWorld.getEntitiesByClass(Enderman.class)) {

                    Location backUp = man.getLocation();
                    backUp.setY(0);

                    if (eggLocation.distance(backUp) <= 35) {
                        if (endermanschoosed < 4) {
                            endermanschoosed = endermanschoosed + 1;
                            endermen.add(man);
                        }
                    }
                }
                if (!endermen.isEmpty()) {
                    for (Enderman mans : endermen) {
                        AreaEffectCloud a = (AreaEffectCloud) main.endWorld.spawnEntity(main.endWorld.getHighestBlockAt(mans.getLocation()).getLocation().add(0, 1, 0), EntityType.AREA_EFFECT_CLOUD);
                        a.setRadius(10.0F);
                        a.setParticle(Particle.VILLAGER_HAPPY);
                        a.setColor(Color.GREEN);

                        a.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 999999, 0), false);

                        mans.setInvulnerable(true);
                    }
                }
            } else if (currentAttack == DemonCurrentAttack.LIGHTING_RAIN) {
                lightingRain = true;
                lightingDuration = 5;
            } else if (currentAttack == DemonCurrentAttack.NIGHT_VISION) {
                nightVision = true;
                nightVisionDuration = 5;
                for (Player all : main.endWorld.getPlayers()) {
                    all.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 7, 0));
                }
            }
        }
    }

    private void tickTnTAttack() {
        timeForTnT = timeForTnT - 1;

        if (timeForTnT == 0) {

            if (enderDragon.getPhase() != EnderDragon.Phase.DYING && !attack360 && enderDragon.getLocation().distance(eggLocation) >= 15) {

                TNTPrimed tnt1 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(3, 0, -3), EntityType.PRIMED_TNT);
                tnt1.setFuseTicks(60);
                tnt1.setYield(tnt1.getYield() * 2);
                tnt1.setCustomName("dragontnt");
                tnt1.setCustomNameVisible(false);

                TNTPrimed tnt2 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(3, 0, 3), EntityType.PRIMED_TNT);
                tnt2.setFuseTicks(60);
                tnt2.setYield(tnt2.getYield() * 2);
                tnt2.setCustomName("dragontnt");
                tnt2.setCustomNameVisible(false);

                TNTPrimed tnt3 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(3, 0, 0), EntityType.PRIMED_TNT);
                tnt3.setFuseTicks(60);
                tnt3.setYield(tnt3.getYield() * 2);
                tnt3.setCustomName("dragontnt");
                tnt3.setCustomNameVisible(false);

                //

                TNTPrimed tnt4 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(-3, 0, 3), EntityType.PRIMED_TNT);
                tnt4.setFuseTicks(60);
                tnt4.setYield(tnt4.getYield() * 2);
                tnt4.setCustomName("dragontnt");
                tnt4.setCustomNameVisible(false);

                TNTPrimed tnt5 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(-3, 0, -3), EntityType.PRIMED_TNT);
                tnt5.setFuseTicks(60);
                tnt5.setYield(tnt5.getYield() * 2);
                tnt5.setCustomName("dragontnt");
                tnt5.setCustomNameVisible(false);

                TNTPrimed tnt6 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(-3, 0, 0), EntityType.PRIMED_TNT);
                tnt6.setFuseTicks(60);
                tnt6.setYield(tnt6.getYield() * 2);
                tnt6.setCustomName("dragontnt");
                tnt6.setCustomNameVisible(false);
            }
            timeForTnT = 30 + (random.nextInt(61));
        }
    }

    private void tickLightingRain() {
        if (lightingRain) {
            if (lightingDuration >= 1) {
                canMakeAnAttack = false;
                lightingDuration = lightingDuration - 1;

                for (Player all : main.endWorld.getPlayers()) {

                    main.endWorld.strikeLightning(all.getLocation());

                    if (currentDemonPhase == DemonPhase.ENRAGED) {

                        all.damage(1.0D);
                    }
                }
            } else {
                lightingRain = false;
                lightingDuration = 5;
                canMakeAnAttack = true;
            }
        }
    }

    private void tickNightVision() {
        if (nightVision) {
            if (nightVisionDuration >= 1) {
                nightVisionDuration--;
            } else {
                for (Player all : main.endWorld.getPlayers()) {
                    if (currentDemonPhase == DemonPhase.NORMAL) {

                        Location highest = main.endWorld.getHighestBlockAt(all.getLocation()).getLocation().add(0, 1, 0);

                        AreaEffectCloud eff = (AreaEffectCloud) main.endWorld.spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);

                        eff.setParticle(Particle.DAMAGE_INDICATOR);
                        eff.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20 * 5, 1), false);
                        eff.setRadius(3.0F);
                    } else {

                        Location highest = main.endWorld.getHighestBlockAt(all.getLocation()).getLocation();

                        AreaEffectCloud eff = (AreaEffectCloud) main.endWorld.spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);

                        eff.setParticle(Particle.DAMAGE_INDICATOR);
                        eff.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20 * 5, 1), false);
                        eff.setRadius(3.0F);
                    }
                }

                nightVision = false;
                canMakeAnAttack = true;
            }
        }
    }

    public void chooseAnAttack() {
        int ran = random.nextInt(25);
        if (ran <= 3) {
            currentAttack = DemonCurrentAttack.LIGHTING_RAIN;
        } else if (ran >= 4 && ran <= 15) {
            currentAttack = DemonCurrentAttack.ENDERMAN_BUFF;
        } else if (ran >= 15 && ran <= 25) {
            currentAttack = DemonCurrentAttack.NIGHT_VISION;
        }
    }


    public Map<Location, Integer> getRegenTime() {
        return regenTime;
    }

    public void setDied(boolean died) {
        isDied = died;
    }

    public Entity getEnderDragon() {
        return enderDragon;
    }

    public boolean isDied() {
        return isDied;
    }

    public Main getMain() {
        return main;
    }

    public void start360attack() {

        this.attack360 = true;
    }

    public DemonPhase getCurrentDemonPhase() {
        return currentDemonPhase;
    }

    public void setCurrentDemonPhase(DemonPhase currentDemonPhase) {
        this.currentDemonPhase = currentDemonPhase;
    }
}
