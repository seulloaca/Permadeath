package tech.sebazcrc.permadeath.nms.v1_15_R1.entity;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Pig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.util.NMS;

import java.lang.reflect.Field;
import java.util.*;

public class SpecialPig extends EntityPig {

    public SpecialPig(Location loc) {
        super(EntityTypes.PIG, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        PathfinderGoalSelector goalSelector = this.goalSelector;
        PathfinderGoalSelector targetSelector = this.targetSelector;

        NMS.getAccessor().clearEntityPathfinders(goalSelector, targetSelector);

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(40.0D);

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));

        this.targetSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));

        ArrayList<String> effectList = new ArrayList<>();
        Pig pig = (Pig) getBukkitEntity();

        effectList.add("SPEED");
        effectList.add("REGENERATION");
        effectList.add("INCREASE_DAMAGE");
        effectList.add("INVISIBILITY");
        effectList.add("JUMP");
        effectList.add("SLOW_FALLING");
        effectList.add("GLOWING");
        effectList.add("DAMAGE_RESISTANCE");

        for (int i = 0; i < 5; i++) {

            Random rand = new Random();

            int randomIndex = rand.nextInt(effectList.size());
            String randomEffectName = effectList.get(randomIndex);

            if (randomEffectName.equals("SPEED")) { // Velocidad III
                int effectLevel = 2;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, effectLevel));
            }

            if (randomEffectName.equals("REGENERATION")) { // Regeneración IV
                int effectLevel = 3;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, effectLevel));
            }

            if (randomEffectName.equals("INCREASE_DAMAGE")) { // Fuerza IV
                int effectLevel = 3;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, effectLevel));
            }

            if (randomEffectName.equals("INVISIBILITY")) { // Invisibilidad
                int effectLevel = 0;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, effectLevel));
            }

            if (randomEffectName.equals("JUMP")) { // Salto V
                int effectLevel = 4;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, effectLevel));
            }

            if (randomEffectName.equals("SLOW_FALLING")) { // Caida lenta
                int effectLevel = 0;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 9999999, effectLevel));
            }

            if (randomEffectName.equals("GLOWING")) { // Brillo
                int effectLevel = 0;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999999, effectLevel));
            }

            if (randomEffectName.equals("DAMAGE_RESISTANCE")) { // Resistencia III
                int effectLevel = 2;
                pig.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, effectLevel));
            }
        }
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return true;
    }
}