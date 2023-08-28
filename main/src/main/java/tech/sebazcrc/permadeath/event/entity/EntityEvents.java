package tech.sebazcrc.permadeath.event.entity;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.manager.Data.PlayerDataManager;
import tech.sebazcrc.permadeath.util.manager.EntityTeleport;

import java.util.concurrent.ThreadLocalRandom;

public class EntityEvents implements Listener {

    @EventHandler
    public void onVD(VehicleDestroyEvent e) {
        if (e.getVehicle().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "module_minecart"), PersistentDataType.BYTE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            Creeper c = (Creeper) e.getEntity();
            if (c.hasMetadata("nether_creeper")) {
                if (e.blockList() != null) {
                    e.blockList().forEach(block -> {
                        if (block.getType() != Material.BEDROCK) block.setType(Material.MAGMA_BLOCK);
                    });
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBreakSkull(EntityPickupItemEvent e) {

        if (e.isCancelled()) return;

        if (e.getEntity() instanceof Player) {
            ItemStack i = e.getItem().getItemStack();

            if (i.getType() == Material.PLAYER_HEAD) {

                SkullMeta meta = (SkullMeta) i.getItemMeta();

                PlayerDataManager man = new PlayerDataManager(meta.getOwner(), Main.instance);
                man.craftHead(i);
            }

            if (i.getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING && Main.instance.getDay() >= 50) {
            if (e.getEntity() instanceof Player) {
                if (Main.instance.getDay() < 60) {
                    e.setDamage(5.0D);
                } else {
                    e.setDamage(10.0D);
                }
            }
        }

        if (e.getEntity().getType() == EntityType.DROPPED_ITEM && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && e.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
            Item item = (Item) e.getEntity();
            if (item.getItemStack().getType() == Material.SHULKER_SHELL) {
                e.setCancelled(true);
            }
        }

        // TP de entidades
        //if (e.getEntity() instanceof Creeper && instance.getDays() >= 50 && e.getEntity().getWorld().getEnvironment() == World.Environment.NORMAL) {
        if (e.getEntity() instanceof Creeper || e.getEntity() instanceof Ghast) {
            new EntityTeleport(e.getEntity(), e);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if (Main.getInstance().getDay() >= 50) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof PolarBear) {

                Player p = (Player) e.getEntity();
                PolarBear b = (PolarBear) e.getDamager();

                b.setAI(false);

                p.getWorld().playSound(b.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
                final Location l = b.getLocation();

                Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                    @Override
                    public void run() {

                        l.getWorld().createExplosion(l, 1.5f, true, false, b);
                        b.remove();
                    }
                }, 10L);

                e.setCancelled(true);
            }
            if (e.getEntity() instanceof Player && e.getDamager() instanceof LlamaSpit) {

                Player p = (Player) e.getEntity();

                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30 * 20, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 0));

                p.setVelocity(p.getVelocity().multiply(3));
            }
        }

        if (Main.instance.getDay() >= 60) {

            if (e.getDamager() instanceof Drowned) {
                e.setDamage(e.getDamage() * 3);
            }
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {

            if (Main.getInstance().getDay() >= 40) {

                e.setCancelled(false);
            } else if (Main.getInstance().getDay() <= 39) {

                e.setCancelled(true);
            }
        }

        if (e.getDamager().getType() == EntityType.FIREBALL) {
            Fireball f = (Fireball) e.getDamager();
            if (f.getShooter() instanceof Ghast) {
                Ghast ghast = (Ghast) f.getShooter();
                if (ghast.getPersistentDataContainer().has(new NamespacedKey(Main.instance, "demonio_flotante"), PersistentDataType.BYTE)) {
                    Entity entity = e.getEntity();
                    if (entity instanceof LivingEntity) {
                        LivingEntity liv = (LivingEntity) entity;
                        liv.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 5, 49));
                        liv.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 4));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFireBallHit(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Ghast && Main.instance.getDay() >= 25) {

            Ghast ghast = (Ghast) e.getEntity().getShooter();
            Fireball f = (Fireball) e.getEntity();
            int yield = (e.getEntity().getWorld().getEnvironment() == World.Environment.THE_END || Main.instance.getDay() >= 50 ? 6 : ThreadLocalRandom.current().nextInt(3, 5 + 1));

            if (ghast.getPersistentDataContainer().has(new NamespacedKey(Main.instance, "demonio_flotante"), PersistentDataType.BYTE))
                yield = 0;

            if (e.getEntity() instanceof Fireball) f.setYield(yield);
        }
    }
}
