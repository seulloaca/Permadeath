package tech.sebazcrc.permadeath.nms.v1_15_R1.entity;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

public class CustomCod extends EntityCod {

    public CustomCod(Location loc) {
        super(EntityTypes.COD, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeMap().b(GenericAttributes.ATTACK_KNOCKBACK);

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(30.0D);
        this.getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).setValue(1500.0D);
    }

    @Override
    public void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return true;
    }
}