package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.util.NMS;

import java.util.ArrayList;
import java.util.Random;

public class SpecialPig extends net.minecraft.world.entity.animal.Pig {

    public SpecialPig(Location loc) {
        super(EntityType.PIG, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        NMS.getAccessor().registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 40.0D, (LivingEntity) getBukkitEntity());

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
        this.setPersistenceRequired(false);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}