package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import tech.sebazcrc.permadeath.util.NMS;

public class CustomCod extends Cod {

    public CustomCod(Location loc) {
        super(EntityType.COD, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        NMS.getAccessor().registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 30.0D, (LivingEntity) this.getBukkitEntity());
        NMS.getAccessor().registerAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK, 1500.0D, (LivingEntity) this.getBukkitEntity());

        this.setPersistenceRequired(false);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}