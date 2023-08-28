package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

public class SpecialBee extends Bee {

    public SpecialBee(Location loc) {
        super(EntityType.BEE, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12.0D);
        this.setHealth(100.0F);
        this.setRemainingPersistentAngerTime(1);

        //this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.setPersistenceRequired(false);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }
}