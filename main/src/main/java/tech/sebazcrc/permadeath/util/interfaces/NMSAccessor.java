package tech.sebazcrc.permadeath.util.interfaces;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface NMSAccessor {

    void setMaxHealth(LivingEntity entity, Double d, boolean setHealth);

    double getMaxHealth(LivingEntity entity);

    void registerAttribute(Attribute a, double value, LivingEntity who);

    void registerHostileMobs();

    void injectHostilePathfinders(LivingEntity entity);

    void drown(Player p, double amount);

    void clearEntityPathfinders(Object goalSelector, Object targetSelector);
}
