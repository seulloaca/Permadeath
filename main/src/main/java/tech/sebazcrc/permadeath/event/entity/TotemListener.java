package tech.sebazcrc.permadeath.event.entity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.util.Objects;

public class TotemListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void totemNerf(EntityResurrectEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (((Player) event.getEntity()).getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING || ((Player) event.getEntity()).getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {

            if (!Main.instance.getConfig().getBoolean("TotemFail.Enable")) return;

            Player p = (Player) event.getEntity();
            String player = p.getName();

            int failProb = 0;
            boolean containsDay;

            if (Main.getInstance().getConfig().contains("TotemFail.FailProbs." + Main.getInstance().getDay())) {
                failProb = Objects.requireNonNull(Objects.requireNonNull(Main.instance.getConfig().getInt("TotemFail.FailProbs." + Main.getInstance().getDay())));
                containsDay = true;
            } else {
                System.out.println("[INFO] La probabilidad del tótem se encuentra desactivada para el día: " + Main.getInstance().getDay());
                containsDay = false;
            }

            String totemFail = Objects.requireNonNull(Main.instance.getConfig().getString("TotemFail.ChatMessage"));
            String totemMessage = Objects.requireNonNull(Main.instance.getConfig().getString("TotemFail.PlayerUsedTotemMessage"));

            if (Main.getInstance().getDay() >= 40) {
                if (Main.getInstance().getDay() < 60) {
                    totemMessage = Objects.requireNonNull(Main.instance.getConfig().getString("TotemFail.PlayerUsedTotemsMessage").replace("{ammount}", "dos").replace("%player%", player));
                } else {
                    totemMessage = Objects.requireNonNull(Main.instance.getConfig().getString("TotemFail.PlayerUsedTotemsMessage").replace("{ammount}", "tres").replace("%player%", player));
                }
            }


            for (String k : Main.instance.getConfig().getConfigurationSection("TotemFail.FailProbs").getKeys(false)) {
                try {
                    int i = Integer.valueOf(k);
                    if (i == Main.getInstance().getDay()) {
                        containsDay = true;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Ha ocurrido un error al cargar la probabilidad de tótem del día '" + k + "'");
                }
            }

            if (!containsDay) return;

            if (failProb >= 101) failProb = 100;
            if (failProb < 0) failProb = 1;

            if (failProb == 100) {
                Bukkit.broadcastMessage(TextUtils.format(totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(100)).replace("%number%", String.valueOf(failProb))));
                Bukkit.broadcastMessage(TextUtils.format(totemFail.replace("%player%", player)));
                event.setCancelled(true);
            } else {

                int random = (int) (Math.random() * 100) + 1;

                int resta = 100 - failProb;
                int toShow = resta;

                if (resta == random) toShow = toShow - 1;

                int raShow = random;

                if (random == resta) raShow = raShow - 1;

                if (Main.instance.getDay() < 40) {

                    if (doPlayerHaveSpecialTotem(p)) {
                        ItemStack s = getTotem(p);
                        p.getInventory().removeItem(s);
                        Bukkit.broadcastMessage(TextUtils.format(Main.instance.getConfig().getString("TotemFail.Medalla").replace("%player%", p.getName())));
                        return;
                    }

                    if (random > resta) {
                        Bukkit.broadcastMessage(TextUtils.format(totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        Bukkit.broadcastMessage(TextUtils.format(totemFail.replace("%player%", player)));
                        event.setCancelled(true);
                    } else {
                        Bukkit.broadcastMessage(TextUtils.format(totemMessage.replace("%player%", player).replace("%porcent%", "!=").replace("%totem_fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(resta))));
                    }
                } else {
                    int neededTotems = (Main.instance.getDay() < 60 ? 2 : 3);
                    int totems = p.getInventory().all(Material.TOTEM_OF_UNDYING).size();

                    if (p.getInventory().getItemInOffHand() != null && p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING)
                        totems++;

                    int removedTotems = 0;
                    boolean hasTotem = doPlayerHaveSpecialTotem(p);

                    if (hasTotem) {
                        ItemStack s = getTotem(p);

                        if (getSpecialTotem(p) == EnumPlayerTotemSlot.OFF_HAND) {
                            p.getInventory().setItemInOffHand(null);
                        } else {
                            p.getInventory().removeItem(s);
                        }
                        removedTotems++;
                    } else {
                        if (p.getInventory().getItemInOffHand() != null && p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                            p.getInventory().setItemInOffHand(null);
                            removedTotems++;
                        }
                    }

                    for (ItemStack s : p.getInventory().getContents()) {
                        if (s != null) {
                            if (s.getType() == Material.TOTEM_OF_UNDYING) {
                                if (removedTotems < neededTotems) {
                                    p.getInventory().removeItem(s);
                                    removedTotems++;
                                }
                            }
                        }
                    }

                    if (totems < neededTotems) {
                        Bukkit.broadcastMessage(TextUtils.format(Main.instance.getConfig().getString("TotemFail.NotEnoughTotems").replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        event.setCancelled(true);
                        return;
                    }

                    if (hasTotem) {
                        Bukkit.broadcastMessage(TextUtils.format(Main.instance.getConfig().getString("TotemFail.Medalla").replace("%player%", p.getName())));
                        return;
                    }

                    if (random > resta) {
                        Bukkit.broadcastMessage(TextUtils.format(totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        Bukkit.broadcastMessage(TextUtils.format(Main.instance.getConfig().getString("TotemFail.ChatMessageTotems").replace("%player%", player)));
                        event.setCancelled(true);
                    } else {

                        Bukkit.broadcastMessage(TextUtils.format(totemMessage.replace("%player%", player).replace("%porcent%", "!=").replace("%totem_fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(resta))));
                    }
                }
            }
        }
    }

    private ItemStack getTotem(Player p) {
        return getSpecialTotem(p) == EnumPlayerTotemSlot.MAIN_HAND ? p.getInventory().getItemInMainHand() : p.getInventory().getItemInOffHand();
    }

    private EnumPlayerTotemSlot getSpecialTotem(Player p) {
        ItemStack main = p.getInventory().getItemInMainHand();
        ItemStack off = p.getInventory().getItemInOffHand();

        if (isSpecial(main)) {
            return EnumPlayerTotemSlot.MAIN_HAND;
        } else if (isSpecial(off)) {
            return EnumPlayerTotemSlot.OFF_HAND;
        } else {
            return null;
        }
    }

    private boolean doPlayerHaveSpecialTotem(Player p) {
        boolean tieneMedalla = false;

        if (p.getInventory().getItemInMainHand() != null) {
            if (p.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING && p.getInventory().getItemInMainHand().getItemMeta().isUnbreakable()) {
                tieneMedalla = true;
            }
        }

        if (p.getInventory().getItemInOffHand() != null) {
            if (p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING && p.getInventory().getItemInOffHand().getItemMeta().isUnbreakable()) {
                tieneMedalla = true;
            }
        }

        return tieneMedalla;
    }

    private boolean isSpecial(ItemStack off) {
        return off != null && off.getType() == Material.TOTEM_OF_UNDYING && off.getItemMeta().isUnbreakable();
    }

    public enum EnumPlayerTotemSlot {
        MAIN_HAND, OFF_HAND
    }
}
