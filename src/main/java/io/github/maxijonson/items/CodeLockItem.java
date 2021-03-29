package io.github.maxijonson.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class CodeLockItem extends Item {
    public static String ID = "codelock";

    private static ItemMeta defaultMeta = null;

    public CodeLockItem() {
        this(1);
    }

    public CodeLockItem(int amount) {
        super(ID, Material.WARPED_BUTTON, amount);

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
    }
}
