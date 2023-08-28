package tech.sebazcrc.permadeath.util.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.item.InfernalNetherite;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;
import tech.sebazcrc.permadeath.util.TextUtils;

public class RecipeManager {

    private Main instance;

    public RecipeManager(Main instance) {
        this.instance = instance;
    }

    public void registerRecipes() {

        try {
            registerHyperGAP();
            registerSuperGAP();
            registerShulkerUnCraft();
            registerEndRel();
        } catch (IllegalStateException ex) {
            // Ignorar, la receta fue registrada antes probablemente
        }
    }

    public void registerD50Recipes() {
        try {
            registerINH();
            registerINC();
            registerINL();
            registerINB();
        } catch (IllegalStateException ex) {
            // Ignorar, la receta fue registrada antes probablemente
        }
    }

    public void registerD60Recipes() {
        try {
            registerIE();
            registerLifeOrb();
            registerBeginningRelic();
        } catch (IllegalStateException ex) {
            // Ignorar, la receta fue registrada antes probablemente
        }
    }

    private void registerBeginningRelic() {

        ItemStack s = PermadeathItems.createBeginningRelic();

        NamespacedKey key = new NamespacedKey(instance, "beginning_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("SBS", "BDB", "SBS");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('D', Material.LIGHT_BLUE_DYE);
        recipe.setIngredient('S', Material.SHULKER_SHELL);
        instance.getServer().addRecipe(recipe);
    }

    private void registerIE() {

        ItemStack s = PermadeathItems.craftInfernalElytra();

        NamespacedKey key = new NamespacedKey(instance, "infernal_elytra");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("III", "IPI", "III");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.ELYTRA);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINH() {
        ItemStack s = InfernalNetherite.craftNetheriteHelmet();

        NamespacedKey key = new NamespacedKey(instance, "infernal_helmet");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_HELMET);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINC() {
        ItemStack s = InfernalNetherite.craftNetheriteChest();

        NamespacedKey key = new NamespacedKey(instance, "infernal_chestplate");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_CHESTPLATE);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINL() {
        ItemStack s = InfernalNetherite.craftNetheriteLegs();

        NamespacedKey key = new NamespacedKey(instance, "infernal_leggings");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_LEGGINGS);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINB() {
        ItemStack s = InfernalNetherite.craftNetheriteBoots();

        NamespacedKey key = new NamespacedKey(instance, "infernal_boots");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_BOOTS);
        instance.getServer().addRecipe(recipe);
    }


    private void registerEndRel() {

        ItemStack s = PermadeathItems.crearReliquia();

        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(instance, "end_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" S ", " D ", " S ");
        recipe.setIngredient('S', Material.SHULKER_SHELL);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        instance.getServer().addRecipe(recipe);
    }

    private void registerShulkerUnCraft() {

        for (Material m : Material.values()) {
            if (m.name().toLowerCase().contains("shulker_box")) {
                Bukkit.addRecipe(new ShapelessRecipe(new NamespacedKey(instance, m.name() + "_uncraft"), new ItemStack(Material.SHULKER_SHELL, 2)).addIngredient(m));
            }
        }
    }

    private void registerHyperGAP() {

        ItemStack s = new ItemBuilder(Material.GOLDEN_APPLE, 1).setDisplayName(TextUtils.format("&6Hyper Golden Apple +")).addEnchant(Enchantment.ARROW_INFINITE, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();
        String id = "hyper_golden_apple";
        NamespacedKey key = new NamespacedKey(instance, id);
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);

        try {
            instance.getServer().addRecipe(recipe);
        } catch (Exception x) {
        }
    }

    private void registerSuperGAP() {

        ItemStack s = new ItemBuilder(Material.GOLDEN_APPLE, 1).setDisplayName(TextUtils.format("&6Super Golden Apple +")).addEnchant(Enchantment.ARROW_INFINITE, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();

        NamespacedKey key = new NamespacedKey(instance, "super_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);
        instance.getServer().addRecipe(recipe);
    }

    private void registerLifeOrb() {

        ItemStack s = PermadeathItems.createLifeOrb();

        NamespacedKey key = new NamespacedKey(instance, "PERMADEATHCORE_LIFO");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("DGB", "RSE", "NOL");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.BONE_BLOCK);
        recipe.setIngredient('R', Material.BLAZE_ROD);
        recipe.setIngredient('S', Material.HEART_OF_THE_SEA);
        recipe.setIngredient('E', Material.END_STONE);
        recipe.setIngredient('N', Material.NETHER_BRICKS);
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('L', Material.LAPIS_BLOCK);
        instance.getServer().addRecipe(recipe);
    }
}
