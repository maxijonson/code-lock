package io.github.maxijonson.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.maxijonson.Utils;

public abstract class Item extends ItemStack {
    public static String NSK_ID = "item.id";

    public Item(String id, Material type) {
        this(id, type, 1);
    }

    public Item(String id, Material type, int amount) {
        super(type, amount);
        Utils.Meta.setCustomData(this, NSK_ID, id);
    }

    public static boolean matchId(ItemMeta meta, String id) {
        String metaId = Utils.Meta.getCustomData(meta, NSK_ID, PersistentDataType.STRING);

        if (metaId == null) {
            return false;
        }

        return metaId.equals(id);
    }

    public static boolean matchId(ItemStack item, String id) {
        return matchId(item.getItemMeta(), id);
    }
}
