package tech.sebazcrc.permadeath.nms.v1_16_R3.entity;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import tech.sebazcrc.permadeath.util.NMS;

public class UltraRavager extends EntityRavager {

    public UltraRavager(Location loc) {
        super(EntityTypes.RAVAGER, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        NMS.getAccessor().clearEntityPathfinders(this.goalSelector, this.targetSelector);

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.4D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(2, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0]));
        this.goalSelector.a(1, new a());

        this.targetSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));

    }

    class a extends PathfinderGoalMeleeAttack {
        public a() {
            super(UltraRavager.this, 1.0D, true);
        }

        protected double a(EntityLiving entityliving) {
            float f = UltraRavager.this.getWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + entityliving.getWidth());
        }
    }
}