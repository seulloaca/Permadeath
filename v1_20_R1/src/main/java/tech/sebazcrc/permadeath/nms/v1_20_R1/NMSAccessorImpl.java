package tech.sebazcrc.permadeath.nms.v1_20_R1;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import tech.sebazcrc.permadeath.util.interfaces.NMSAccessor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class NMSAccessorImpl implements NMSAccessor {
    private Field ATTRIBUTE_MAP_FIELD;

    public NMSAccessorImpl() {
        try {
            ATTRIBUTE_MAP_FIELD = AttributeMap.class.getDeclaredField("b");
            ATTRIBUTE_MAP_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setMaxHealth(LivingEntity entity, Double d, boolean setHealth) {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(d);
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
        return entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerAttribute(Attribute a, double value, LivingEntity who) {
        net.minecraft.world.entity.LivingEntity e = ((CraftLivingEntity) who).getHandle();
        if (!(e instanceof Mob)) return;

        net.minecraft.world.entity.ai.attributes.Attribute serverAttribute = bukkitToNMSAttribute(a);
        try {
            AttributeInstance attributeModifiable = new AttributeInstance(serverAttribute, AttributeInstance::getAttribute);

            Map<net.minecraft.world.entity.ai.attributes.Attribute, AttributeInstance> map = (Map<net.minecraft.world.entity.ai.attributes.Attribute, AttributeInstance>) ATTRIBUTE_MAP_FIELD.get(e.getAttributes());
            map.put(serverAttribute, attributeModifiable);

            ATTRIBUTE_MAP_FIELD.set(e.getAttributes(), map);
        } catch (IllegalArgumentException ignored) {} catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        e.getAttribute(serverAttribute).setBaseValue(value);
    }

    private net.minecraft.world.entity.ai.attributes.Attribute bukkitToNMSAttribute(Attribute a) {
        switch (a) {
            case GENERIC_ARMOR:
                return Attributes.ARMOR;
            case GENERIC_ARMOR_TOUGHNESS:
                return Attributes.ARMOR_TOUGHNESS;
            case GENERIC_ATTACK_DAMAGE:
                return Attributes.ATTACK_DAMAGE;
            case GENERIC_LUCK:
                return Attributes.LUCK;
            case GENERIC_MAX_HEALTH:
                return Attributes.MAX_HEALTH;
            case HORSE_JUMP_STRENGTH:
                return Attributes.JUMP_STRENGTH;
            case GENERIC_ATTACK_SPEED:
                return Attributes.ATTACK_SPEED;
            case GENERIC_FLYING_SPEED:
                return Attributes.FLYING_SPEED;
            case GENERIC_FOLLOW_RANGE:
                return Attributes.FOLLOW_RANGE;
            case GENERIC_MOVEMENT_SPEED:
                return Attributes.MOVEMENT_SPEED;
            case GENERIC_ATTACK_KNOCKBACK:
                return Attributes.ATTACK_KNOCKBACK;
            case GENERIC_KNOCKBACK_RESISTANCE:
                return Attributes.KNOCKBACK_RESISTANCE;
        }
        return null;
    }

    @Override
    public void registerHostileMobs() {

    }

    @Override
    public void injectHostilePathfinders(LivingEntity entity) {
        Entity nms = ((CraftEntity)entity).getHandle();

        if (nms instanceof PathfinderMob) {
            PathfinderMob insentient = (PathfinderMob) nms;
            if (entity.getType() != EntityType.LLAMA && entity.getType() != EntityType.PANDA) {
                GoalSelector goalSelector = insentient.goalSelector;

                AtomicBoolean containsMeleeGoal = new AtomicBoolean(false);
                goalSelector.removeAllGoals(goal -> {
                    if (goal.getClass() == MeleeAttackGoal.class) {
                        containsMeleeGoal.set(true);
                    }
                    return (goal.getClass() == AvoidEntityGoal.class || goal.getClass() == PanicGoal.class);
                });
                if (!containsMeleeGoal.get()) {
                    goalSelector.addGoal(0, new MeleeAttackGoal(insentient, 1.0D, true));
                }
            }
            GoalSelector targetSelector = insentient.targetSelector;
            targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(insentient, net.minecraft.world.entity.player.Player.class, true));
        }
    }

    @Override
    public void drown(Player p, double amount) {
        ServerPlayer serverPlayer = ((CraftPlayer)p).getHandle();
        serverPlayer.hurt(serverPlayer.damageSources().drown(), (float) amount);
    }

    @Override
    public void clearEntityPathfinders(Object goalSelector, Object targetSelector) {
        GoalSelector goals = (GoalSelector) goalSelector, targets = (GoalSelector) targetSelector;
        goals.removeAllGoals((goal) -> true);
        targets.removeAllGoals((goal) -> true);
    }
}
