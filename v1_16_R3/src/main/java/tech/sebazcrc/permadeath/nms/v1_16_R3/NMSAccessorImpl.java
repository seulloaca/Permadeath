package tech.sebazcrc.permadeath.nms.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import tech.sebazcrc.permadeath.util.interfaces.NMSAccessor;

import java.lang.reflect.Field;
import java.util.*;

public class NMSAccessorImpl implements NMSAccessor {
    private Field GOAL_SELECTOR_PATHFINDER_SET_FIELD, GOAL_SELECTOR_PATHFINDER_MAP_FIELD, GOAL_SELECTOR_PATHFINDER_ENUMSET_FIELD;
    private Field WRAPPED_PATHFINDER_GOAL_FIELD;
    private Field ATTRIBUTE_MAP_FIELD;

    public NMSAccessorImpl() {
        try {
            GOAL_SELECTOR_PATHFINDER_SET_FIELD = PathfinderGoalSelector.class.getDeclaredField("d");
            GOAL_SELECTOR_PATHFINDER_SET_FIELD.setAccessible(true);

            GOAL_SELECTOR_PATHFINDER_MAP_FIELD = PathfinderGoalSelector.class.getDeclaredField("c");
            GOAL_SELECTOR_PATHFINDER_MAP_FIELD.setAccessible(true);

            GOAL_SELECTOR_PATHFINDER_ENUMSET_FIELD = PathfinderGoalSelector.class.getDeclaredField("f");
            GOAL_SELECTOR_PATHFINDER_ENUMSET_FIELD.setAccessible(true);

            WRAPPED_PATHFINDER_GOAL_FIELD = PathfinderGoalWrapped.class.getDeclaredField("a");
            WRAPPED_PATHFINDER_GOAL_FIELD.setAccessible(true);

            ATTRIBUTE_MAP_FIELD = AttributeMapBase.class.getDeclaredField("b");
            ATTRIBUTE_MAP_FIELD.setAccessible(true);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public void setMaxHealth(LivingEntity entity, Double d, boolean setHealth) {
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(d);
        if (setHealth) {
            entity.setHealth(d);
        }
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
        return Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }
    @Override
    @SuppressWarnings("unchecked")
    public void registerAttribute(Attribute a, double value, LivingEntity who) {
        EntityLiving e = ((CraftLivingEntity) who).getHandle();

        EntityInsentient insentient = (EntityInsentient) e;
        AttributeBase serverAttribute = bukkitToNMSAttribute(a);
        try {
            AttributeModifiable attributeModifiable = new AttributeModifiable(serverAttribute, AttributeModifiable::getAttribute);

            Map<AttributeBase, AttributeModifiable> map = (Map<AttributeBase, AttributeModifiable>) ATTRIBUTE_MAP_FIELD.get(e.getAttributeMap());
            map.put(serverAttribute, attributeModifiable);

            ATTRIBUTE_MAP_FIELD.set(e.getAttributeMap(), map);
        } catch (IllegalArgumentException | IllegalAccessException ignored) {}

        insentient.getAttributeInstance(serverAttribute).setValue(value);
    }

    @Override
    public void registerHostileMobs() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void injectHostilePathfinders(LivingEntity entity) {
        net.minecraft.server.v1_16_R3.Entity nms = ((CraftEntity)entity).getHandle();
        EntityType type = entity.getType();

        if (nms instanceof EntityCreature && type != EntityType.LLAMA && type != EntityType.PANDA) {
            EntityCreature insentient = (EntityCreature) nms;
            PathfinderGoalSelector goalSelector = insentient.goalSelector;

            try {
                Set<PathfinderGoalWrapped> set = (Set<PathfinderGoalWrapped>) GOAL_SELECTOR_PATHFINDER_SET_FIELD.get(goalSelector);
                boolean containsMelee = false;

                PathfinderGoal goal;
                for (PathfinderGoalWrapped w : set) {
                    goal = (PathfinderGoal) WRAPPED_PATHFINDER_GOAL_FIELD.get(w);
                    if (goal.getClass() == PathfinderGoalAvoidTarget.class || goal.getClass() == PathfinderGoalPanic.class) {
                        set.remove(goal);
                    } else if (goal.getClass() == PathfinderGoalMeleeAttack.class) {
                        containsMelee = true;
                    }
                }
                if (!containsMelee) {
                    set.add(new PathfinderGoalWrapped(0, new PathfinderGoalMeleeAttack(insentient, 1.0F, true)));
                }

                GOAL_SELECTOR_PATHFINDER_SET_FIELD.set(goalSelector, set);
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (nms instanceof EntityInsentient) {
            EntityInsentient insentient = (EntityInsentient) nms;
            PathfinderGoalSelector targetSelector = insentient.targetSelector;

            try {
                Set<PathfinderGoalWrapped> set = (Set<PathfinderGoalWrapped>) GOAL_SELECTOR_PATHFINDER_SET_FIELD.get(targetSelector);
                set.add(new PathfinderGoalWrapped(0, new PathfinderGoalNearestAttackableTarget<>(insentient, EntityHuman.class, true)));
                GOAL_SELECTOR_PATHFINDER_SET_FIELD.set(targetSelector, set);
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public AttributeBase bukkitToNMSAttribute(Attribute attribute) {
        AttributeBase ia = null;

        if (attribute == Attribute.GENERIC_ATTACK_DAMAGE) {
            ia = GenericAttributes.ATTACK_DAMAGE;
        }
        if (attribute == Attribute.GENERIC_ARMOR_TOUGHNESS) {
            ia = GenericAttributes.ARMOR_TOUGHNESS;
        }
        if (attribute == Attribute.GENERIC_ARMOR) {
            ia = GenericAttributes.ARMOR;
        }
        if (attribute == Attribute.GENERIC_MAX_HEALTH) {
            ia = GenericAttributes.MAX_HEALTH;
        }
        if (attribute == Attribute.GENERIC_KNOCKBACK_RESISTANCE) {
            ia = GenericAttributes.KNOCKBACK_RESISTANCE;
        }
        if (attribute == Attribute.GENERIC_ATTACK_SPEED) {
            ia = GenericAttributes.ATTACK_SPEED;
        }
        if (attribute == Attribute.GENERIC_FLYING_SPEED) {
            ia = GenericAttributes.FLYING_SPEED;
        }
        if (attribute == Attribute.GENERIC_FOLLOW_RANGE) {
            ia = GenericAttributes.FOLLOW_RANGE;
        }
        if (attribute == Attribute.GENERIC_MOVEMENT_SPEED) {
            ia = GenericAttributes.MOVEMENT_SPEED;
        }
        if (attribute == Attribute.GENERIC_LUCK) {
            ia = GenericAttributes.LUCK;
        }

        return ia;
    }

    @Override
    public void drown(org.bukkit.entity.Player p, double amount) {
        net.minecraft.server.v1_16_R3.Entity en = ((CraftEntity)p).getHandle();
        en.damageEntity(net.minecraft.server.v1_16_R3.DamageSource.DROWN, (float) amount);
    }

    @Override
    public void clearEntityPathfinders(Object goalSelector, Object targetSelector) {

        try {
            GOAL_SELECTOR_PATHFINDER_SET_FIELD.set(goalSelector, new LinkedHashSet<>());

            GOAL_SELECTOR_PATHFINDER_MAP_FIELD.set(goalSelector, new EnumMap<>(PathfinderGoal.Type.class));

            GOAL_SELECTOR_PATHFINDER_ENUMSET_FIELD.set(goalSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            GOAL_SELECTOR_PATHFINDER_SET_FIELD.set(targetSelector, new LinkedHashSet<>());

            GOAL_SELECTOR_PATHFINDER_MAP_FIELD.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            GOAL_SELECTOR_PATHFINDER_ENUMSET_FIELD.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
