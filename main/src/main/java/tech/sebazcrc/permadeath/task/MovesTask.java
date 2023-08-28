package tech.sebazcrc.permadeath.task;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.Main;

import java.util.ArrayList;

public class MovesTask extends BukkitRunnable {

    private float yaw;
    private Main main;
    private EnderDragon dragon;

    int ticksRotating = 0;
    int currentPitchRotation = -360;
    Location teleportLocation;
    boolean spawnedPaticles = false;

    public MovesTask(Main main, EnderDragon dragon, Location tp) {
        this.main = main;
        this.dragon = dragon;
        this.teleportLocation = tp;
    }


    @Override
    public void run() {

        if (dragon.isDead() || main.getTask() == null) {

            cancel();
            return;
        }

        int ticks = 20 * 15;

        if (ticksRotating == ticks) {
            cancel();
            return;
        }

        ticksRotating = ticksRotating + 5;

        if (currentPitchRotation >= 0) {

            currentPitchRotation = -360;
        }

        if (currentPitchRotation < 0) {

            if (dragon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL) {

                dragon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);
            }

            dragon.setRotation(currentPitchRotation, 0);

            currentPitchRotation = currentPitchRotation + 30;
            if (!spawnedPaticles) {

                spawnedPaticles = true;

                ArrayList<Location> locations = new ArrayList<>();
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(7, 0, 7)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(7, 0, 0)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(7, 0, -7)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(0, 0, -7)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(0, 0, 7)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(-7, 0, 7)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(-7, 0, 0)).getLocation());
                locations.add(main.endWorld.getHighestBlockAt(dragon.getLocation().add(-7, 0, -7)).getLocation());

                for (Location locs : locations) {

                    AreaEffectCloud a = (AreaEffectCloud) locs.getWorld().spawnEntity(locs.add(0, 1, 0), EntityType.AREA_EFFECT_CLOUD);

                    a.setParticle(Particle.CLOUD);
                    a.setRadius(5.0F);
                    a.setDuration(15 * 20);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 5, 3), false);
                    a.setColor(Color.WHITE);
                }
            }
        }
    }
}
