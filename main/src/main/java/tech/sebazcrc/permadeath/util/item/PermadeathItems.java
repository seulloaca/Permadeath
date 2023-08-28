package tech.sebazcrc.permadeath.util.item;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.lib.HiddenStringUtils;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.util.Arrays;
import java.util.UUID;

public class PermadeathItems {
    private static final int[] beginningRelicLockedSlots = {40, 34, 33, 32, 30, 29, 28, 27, 26, 25, 24, 23, 21, 20, 19, 18, 17, 16, 15, 14, 12, 11, 10, 9, 8, 7};

    public static ItemStack crearReliquia() {

        ItemStack s = new ItemBuilder(Material.LIGHT_BLUE_DYE).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Reliquia Del Fin")).build();

        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}")));
        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack createLifeOrb() {
        return new ItemBuilder(Material.BROWN_DYE).setCustomModelData(1, !Main.optifineItemsEnabled()).setUnbrekeable(true).setDisplayName(TextUtils.format("&6Orbe de Vida")).build();
    }

    public static ItemStack createBeginningRelic() {
        return new ItemBuilder(Material.CYAN_DYE).setCustomModelData(1, !Main.optifineItemsEnabled()).setUnbrekeable(true).setDisplayName(TextUtils.format("&6Reliquia del Comienzo")).build();
    }

    public static ItemStack craftInfernalElytra() {

        ItemStack s = new ItemBuilder(Material.ELYTRA).setCustomModelData(1).setDisplayName(TextUtils.format("&6Elytras de Netherite Infernal")).build();

        ItemMeta meta = s.getItemMeta();

        AttributeModifier m = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
        AttributeModifier m2 = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);

        assert meta != null;
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, m);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, m2);

        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack craftNetheriteSword() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_SWORD).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Espada de Netherite")).build();
        ItemMeta meta = s.getItemMeta();

        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.4D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        assert meta != null;
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack craftNetheritePickaxe() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_PICKAXE).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Pico de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack craftNetheriteHoe() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_HOE).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Azada de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack craftNetheriteAxe() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_AXE).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Hacha de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 10.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack craftNetheriteShovel() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_SHOVEL).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Pala de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 6.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier2);
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public static ItemStack craftInfernalNetheriteIngot() {

        ItemStack s = new ItemBuilder(Material.DIAMOND).setCustomModelData(1, !Main.optifineItemsEnabled()).setDisplayName(TextUtils.format("&6Infernal Netherite Block")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID() + ": 0}")));
        s.setItemMeta(meta);

        return s;
    }

    public static void slotBlock(Player p) {
        if (Main.getInstance().getDay() < 40) return;
        if (p.getGameMode() == GameMode.SPECTATOR || p.isDead() || !p.isOnline()) return;

        boolean hasEndRelic = false;
        boolean hasBeginningRelic = false;

        int[] endRelicLockedSlots;
        if (Main.getInstance().getDay() < 60) {
            endRelicLockedSlots = new int[]{40, 13, 22, 31, 4};
        } else {
            endRelicLockedSlots = new int[]{13, 22, 31, 4};
        }


        for (ItemStack contents : p.getInventory().getContents()) {
            if (!hasBeginningRelic && isBeginningRelic(contents)) {
                hasBeginningRelic = true;
                hasEndRelic = true;
            } else if (!hasEndRelic && isEndRelic(contents)) {
                hasEndRelic = true;
            }
        }

        int slot;
        if (Main.getInstance().getDay() >= 40) {
            for (int i = 0; i < endRelicLockedSlots.length; i++) {
                slot = endRelicLockedSlots[i];
                if (hasEndRelic) {
                    unlockSlot(p, slot);
                } else {
                    lockSlot(p, slot);
                }
            }
        }

        if (Main.getInstance().getDay() >= 60) {
            for (int i = 0; i < beginningRelicLockedSlots.length; i++) {
                slot = beginningRelicLockedSlots[i];
                if (hasBeginningRelic) {
                    unlockSlot(p, slot);
                } else {
                    lockSlot(p, slot);
                }
            }
        }
    }

    private static void lockSlot(Player p, int slot) {
        ItemStack item = p.getInventory().getItem(slot);

        if (item != null) {

            if (item.getType() != Material.AIR && item.getType() != Material.STRUCTURE_VOID) {
                p.getWorld().dropItem(p.getLocation(), item.clone());
            }

            item.setType(Material.STRUCTURE_VOID);
            item.setAmount(1);
        } else {
            p.getInventory().setItem(slot, new ItemStack(Material.STRUCTURE_VOID));
        }
    }

    private static void unlockSlot(Player p, int slot) {
        ItemStack item = p.getInventory().getItem(slot);

        if (item != null && item.getType() == Material.STRUCTURE_VOID) {
            p.getInventory().clear(slot);
        }
    }

    public static boolean isEndRelic(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        if (stack.getType() == Material.LIGHT_BLUE_DYE && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Reliquia Del Fin"))) {
            return true;
        }
        return false;
    }

    public static boolean isBeginningRelic(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        if (stack.getType() == Material.CYAN_DYE && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Reliquia del Comienzo"))) {
            return true;
        }
        return false;
    }
}
