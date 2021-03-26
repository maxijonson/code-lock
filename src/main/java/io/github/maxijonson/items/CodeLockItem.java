package io.github.maxijonson.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.maxijonson.Utils;

public class CodeLockItem extends Item {
    public static String ID = "codelock";
    public static String NSK_CODE = ID + ".code";

    private static ItemMeta defaultMeta = null;
    public int test = 0;

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

            Utils.Meta.setCustomData(defaultMeta, NSK_CODE, -1);

        }

        this.setItemMeta(defaultMeta);
    }
}
