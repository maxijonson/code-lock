package io.github.maxijonson.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.maxijonson.Utils;

public class CodeLockItem extends Item {
    public static final String ID = "codelock";
    public static final String NSK_TYPE = ID + ".type";
    public static final String RECIPE_GROUP = "codelock." + ID;
    public static final HashMap<NamespacedKey, Material> buttons = new HashMap<NamespacedKey, Material>() {
        private static final long serialVersionUID = 1L;
        {
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "oak"), Material.OAK_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "birch"), Material.BIRCH_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "stone"), Material.STONE_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "acacia"), Material.ACACIA_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "jungle"), Material.JUNGLE_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "spruce"), Material.SPRUCE_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "warped"), Material.WARPED_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "crimson"), Material.CRIMSON_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "dark_oak"), Material.DARK_OAK_BUTTON);
            put(NamespacedKey.minecraft(RECIPE_GROUP + "." + "polished_blackstone"),
                    Material.POLISHED_BLACKSTONE_BUTTON);
        }
    };

    private static ItemMeta defaultMeta = null;

    public CodeLockItem() {
        this(1, Material.OAK_BUTTON);
    }

    public CodeLockItem(int amount) {
        this(amount, Material.OAK_BUTTON);
    }

    public CodeLockItem(Material material) {
        this(1, material);
    }

    public CodeLockItem(String material) {
        this(getMaterial(material));
    }

    public CodeLockItem(int amount, Material material) {
        super(ID, material, amount);

        if (defaultMeta == null) {
            defaultMeta = this.getItemMeta();

            defaultMeta.setDisplayName("Code Lock");
            defaultMeta.addEnchant(Enchantment.LUCK, 1, false);
            defaultMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            List<String> lore = new ArrayList<>();
            lore.add("Locks entities with a code");
            defaultMeta.setLore(lore);
        }

        this.setItemMeta(defaultMeta);
        Utils.Meta.setCustomData(this, NSK_TYPE, PersistentDataType.STRING, material.name());
    }

    private static Material getMaterial(String material) {
        Material m = Material.getMaterial(material);
        if (m == null) {
            m = Material.OAK_BUTTON;
        }
        return m;
    }

    public static void registerRecipe(JavaPlugin plugin) {
        char iron = 'I';
        char redstone = 'R';
        char button = 'B';
        char empty = ' ';
        String[] shape = { String.format("%c%c%c", empty, iron, empty),
                String.format("%c%c%c", redstone, button, redstone), String.format("%c%c%c", empty, empty, empty) };

        // FIXME: We need to loop through EVERY possible button materials. Can't we just
        // get a generic "Button"?
        for (Entry<NamespacedKey, Material> entry : buttons.entrySet()) {
            NamespacedKey key = entry.getKey();
            Material material = entry.getValue();

            ShapedRecipe recipe = new ShapedRecipe(key, new CodeLockItem(material));

            recipe.shape(shape);

            recipe.setIngredient(iron, Material.IRON_INGOT);
            recipe.setIngredient(redstone, Material.REDSTONE);
            recipe.setIngredient(button, material);

            recipe.setGroup(RECIPE_GROUP);

            plugin.getServer().addRecipe(recipe);
        }
    }
}
