package tech.sebazcrc.permadeath.util.manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import tech.sebazcrc.permadeath.Main;

import java.util.SplittableRandom;

public class EntityTeleport {

    private Entity c;
    private EntityDamageEvent e;
    private World world;
    private Main main;
    private double locX;
    private double locY;
    private double locZ;

    private SplittableRandom random;

    public EntityTeleport(Entity c, EntityDamageEvent e) {
        this.c = c;
        this.e = e;
        this.world = c.getWorld();
        this.main = Main.getInstance();
        this.locX = c.getLocation().getX();
        this.locY = c.getLocation().getY();
        this.locZ = c.getLocation().getZ();
        this.random = new SplittableRandom();

        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.VOID) {
            if (main.getFactory().hasData(e.getEntity(), "ender_creeper") || main.getFactory().hasData(e.getEntity(), "ender_quantum_creeper")) {
                teleport();
                e.setCancelled(true);
            }

            if (main.getFactory().hasData(e.getEntity(), "ender_ghast") && random.nextInt(101) <= 20) {
                teleport();
                e.setCancelled(true);
            }

            if (main.getFactory().hasData(e.getEntity(), "tp_ghast") && random.nextInt(101) <= 80) {
                teleport();
                e.setCancelled(true);
            }
        }
    }

    public boolean teleport() {
        for (int i = 0; i < 64; ++i) {
            if (eq()) {
                return true;
            }
        }

        return false;
    }

    private boolean eq() {
        double x = locX + (this.random.nextDouble() - 0.5D) * 64.0D;
        double y = locY + (double) (this.random.nextInt(64) - 32);
        double z = locZ + (this.random.nextDouble() - 0.5D) * 64.0D;

        //BlockActuallyPosition act = new BlockActuallyPosition(this.world.getBlockAt((int)x, (int)y, (int)z));
        Block b = this.world.getBlockAt((int) x, (int) y, (int) z);

        while (b.getY() > 0 && b.getType().isAir()) b = b.getRelative(BlockFace.DOWN);
        //act = act.goDeeper();

        if (b.getY() <= 0) return false;

        return this.c.teleport(new Location(this.world, x, b.getY() + 1, z));
    }

    /**
     private class BlockActuallyPosition {

     private Block block;

     public BlockActuallyPosition(Block start) {
     this.block = start;
     }

     public BlockActuallyPosition goDeeper() {
     this.block = this.block.getRelative(BlockFace.DOWN);
     return this;
     }

     public Block getBlock() {
     return block;
     }
     }
     */
}

