package tech.sebazcrc.permadeath.util.lib;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ItemBuilder {
    protected ItemStack is;

    protected ItemMeta im;

    public ItemBuilder() {
    }

    public ItemBuilder(ItemStack itemStack) {
        this.is = new ItemStack(itemStack);
    }

    public ItemBuilder(Material material) {
        this.is = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.is = new ItemStack(material, amount);
    }

    public ItemBuilder setDurability(int durability) {
        this.is.setDurability((short) durability);
        return this;
    }

    public ItemBuilder setUnbrekeable(boolean b) {
        this.im = this.is.getItemMeta();
        this.im.setUnbreakable(b);
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder setCustomModelData(int model) {
        this.im = this.is.getItemMeta();
        this.im.setCustomModelData(model);
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder setCustomModelData(int model, boolean b) {
        if (!b) return this;

        this.im = this.is.getItemMeta();
        this.im.setCustomModelData(model);
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        this.im = this.is.getItemMeta();
        this.im.setDisplayName(name);
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        this.im = this.is.getItemMeta();
        this.im.addEnchant(enchantment, level, true);
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchantments) {
        this.im = this.is.getItemMeta();
        if (!enchantments.isEmpty())
            for (Enchantment ench : enchantments.keySet())
                this.im.addEnchant(ench, ((Integer) enchantments.get(ench)).intValue(), true);
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemflag) {
        this.im = this.is.getItemMeta();
        this.im.addItemFlags(new ItemFlag[]{itemflag});
        this.is.setItemMeta(this.im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.im = this.is.getItemMeta();
        this.im.setLore(lore);
        this.is.setItemMeta(this.im);
        return this;
    }

    public static String format(String s) {

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public ItemStack build() {
        return this.is;
    }
}
