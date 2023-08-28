package tech.sebazcrc.permadeath.nms.v1_16_R3.entity;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Giant;

public class CustomGiant extends EntityGiantZombie {

    public CustomGiant(Location loc) {
        super(EntityTypes.GIANT, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(2000.0D);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(600.0D);

        Giant giant = (Giant) getBukkitEntity();
        giant.setHealth(600.0D);

        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ZOMBIE_AMBIENT;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOMBIE_DEATH;
    }
}