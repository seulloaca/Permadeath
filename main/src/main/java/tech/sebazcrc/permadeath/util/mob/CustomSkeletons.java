package tech.sebazcrc.permadeath.util.mob;

import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;

public class CustomSkeletons implements Listener {

    private final Main plugin;

    public CustomSkeletons(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void onH(ProjectileHitEvent e) {


        if (!(e.getEntity().getShooter() instanceof Skeleton)) return;

        Skeleton s = (Skeleton) e.getEntity().getShooter();

        if (plugin.getDay() >= 60 && s.getPersistentDataContainer().has(new NamespacedKey(plugin, "demon_skeleton"), PersistentDataType.BYTE)) {

            if (e.getEntity() != null) {

                Entity h = e.getHitEntity();
                h.getWorld().createExplosion(h.getLocation(), 3f, true, true, s);
            } else if (e.getHitBlock() != null) {

                e.getEntity().getWorld().createExplosion(e.getHitBlock().getLocation(), 3f, true, true, s);
            }
        }
    }

    @EventHandler
    public void onNDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Arrow) {
            if (((Arrow) e.getDamager()).getShooter() instanceof Skeleton && ((Skeleton) ((Arrow) e.getDamager()).getShooter()).getPersistentDataContainer().has(new NamespacedKey(plugin, "skeleton_definitivo"), PersistentDataType.BYTE)) {

                try {
                    if (e.getEntity() instanceof LivingEntity) {
                        ((LivingEntity) e.getEntity()).damage(((LivingEntity) e.getEntity()).getHealth());
                    }
                } catch (Exception x) {
                }
            }
        }
        if (e.getDamager() instanceof ShulkerBullet) {
            ShulkerBullet b = (ShulkerBullet) e.getDamager();
            if (b.getShooter() instanceof Shulker && ((Shulker) b.getShooter()).getColor() == DyeColor.RED && e.getEntity().getType() == EntityType.CAVE_SPIDER) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Shulker && ((Shulker) e.getEntity()).getColor() == DyeColor.RED && e.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
            e.setCancelled(true);
        }
    }
}
