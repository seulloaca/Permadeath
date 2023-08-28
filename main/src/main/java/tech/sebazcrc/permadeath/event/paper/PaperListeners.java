package tech.sebazcrc.permadeath.event.paper;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import com.destroystokyo.paper.event.entity.EntityTeleportEndGatewayEvent;
import com.destroystokyo.paper.event.player.PlayerTeleportEndGatewayEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.end.demon.DemonPhase;

import java.util.ArrayList;
import java.util.SplittableRandom;

public class PaperListeners implements Listener {

    private Main main;
    private SplittableRandom random = new SplittableRandom();

    public PaperListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onProjectileHit(EnderDragonFireballHitEvent e) {
        AreaEffectCloud a = e.getAreaEffectCloud();
        if (main.getTask() != null) {

            ArrayList<Block> toChange = new ArrayList<>();

            Block b = main.endWorld.getHighestBlockAt(a.getLocation());
            Location highest = main.endWorld.getHighestBlockAt(a.getLocation()).getLocation();

            int structure = random.nextInt(4);
            if (structure == 0) {
                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.SOUTH_EAST));
                toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
            } else if (structure == 1) {

                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                toChange.add(b);
            } else if (structure == 2) {

                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                toChange.add(b);
            } else if (structure == 3) {

                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                toChange.add(b);
                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.EAST));
            } else if (structure == 4) {

                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.NORTH_WEST));
                toChange.add(b);
                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.WEST));
            }

            if (main.getTask().getCurrentDemonPhase() == DemonPhase.NORMAL) {
                if (highest.getY() > 0) {

                    for (Block all : toChange) {
                        Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();
                        Block now = main.endWorld.getBlockAt(used);
                        if (now.getType() != Material.AIR) {
                            now.setType(Material.BEDROCK);
                        }
                    }
                }
            } else {

                if (random.nextBoolean()) {
                    a.setParticle(Particle.SMOKE_NORMAL);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 1), false);
                } else {
                    if (highest.getY() > 0) {
                        for (Block all : toChange) {
                            Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();
                            Block now = main.endWorld.getBlockAt(used);
                            if (now.getType() != Material.AIR) {
                                now.setType(Material.BEDROCK);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGatewayTeleport(EntityTeleportEndGatewayEvent e) {

        if (main.getDay() < 40) return;

        if (main.getDay() >= 50) {
            if (main.getBeginningManager().isClosed()) {
                e.setCancelled(true);
                return;
            }

            Entity entity = e.getEntity();
            Location from = e.getFrom();
            World world = from.getWorld();

            if (entity instanceof Player) return;
            e.setCancelled(true);

            final Vector direction = entity.getLocation().getDirection();
            final Vector velocity = entity.getVelocity();
            Float pitch = entity.getLocation().getPitch();
            Float yaw = entity.getLocation().getYaw();

            if (world.getName().equalsIgnoreCase(main.world.getName())) {

                Location loc = main.getBeData().getBeginningPortal();
                loc.setDirection(direction);
                loc.setPitch(pitch);
                loc.setYaw(yaw);
                entity.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                entity.setVelocity(velocity);
            }

            if (world.getName().equalsIgnoreCase("pdc_the_beginning")) {

                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {
                        Location loc = main.world.getSpawnLocation();
                        loc.setDirection(direction);
                        loc.setPitch(pitch);
                        loc.setYaw(yaw);
                        entity.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        entity.setVelocity(velocity);
                    }
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onGatewayTeleport(PlayerTeleportEndGatewayEvent e) {

        if (main.getDay() < 40) return;

        if (main.getDay() < 50) {
            if (e.getPlayer().getWorld().getName().equalsIgnoreCase(main.world.getName()) || e.getPlayer().getWorld().getName().equalsIgnoreCase(main.getBeginningManager().getBeginningWorld().getName())) {
                e.getPlayer().setNoDamageTicks(e.getPlayer().getMaximumNoDamageTicks());
                e.getPlayer().damage(e.getPlayer().getHealth() + 1.0D, null);
                e.getPlayer().setNoDamageTicks(0);
                Bukkit.broadcastMessage(TextUtils.format("&c&lEl jugador &4&l" + e.getPlayer().getName() + " &c&lentró a TheBeginning antes de tiempo."));
            }
            return;
        }

        if (main.getDay() >= 50) {

            if (main.getBeginningManager().isClosed()) {

                e.setCancelled(true);
                return;
            }

            EndGateway gateway = e.getGateway();
            Player p = e.getPlayer();

            Location from = e.getFrom();

            World world = from.getWorld();

            gateway.setExitLocation(gateway.getLocation());
            gateway.update();
            e.setCancelled(true);

            final Vector direction = p.getLocation().getDirection();
            final Vector velocity = p.getVelocity();

            if (world.getName().equalsIgnoreCase(main.world.getName())) {

                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {

                        Location loc = main.getBeData().getBeginningPortal();
                        loc.setDirection(direction);
                        p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        p.setVelocity(velocity);
                    }
                }, 1L);
            }

            if (world.getName().equalsIgnoreCase("pdc_the_beginning")) {

                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {
                        Location loc = main.world.getSpawnLocation();
                        loc.setDirection(direction);
                        p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        p.setVelocity(velocity);
                    }
                }, 1L);
            }
        }
    }
}
