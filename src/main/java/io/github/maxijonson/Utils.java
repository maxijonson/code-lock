package io.github.maxijonson;

import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Utils {

    /**
     * Creates a unique ID for a block based on its position and world. It does not
     * temper with the the block itself, the resulting ID should be kept somewhere
     * for future use.
     * 
     * @param block the block to get the ID
     * @return
     */
    public static String getBlockId(Block block) {
        return String.format("%s:%d-%d-%d", block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public static class Meta {
        private static HashMap<String, NamespacedKey> keys = new HashMap<>();

        public static NamespacedKey createNamespacedKey(String key) {
            NamespacedKey nsk = new NamespacedKey(CodeLock.getInstance(), key);
            keys.put(key, nsk);
            System.out.println("Created new key " + key + " (now with " + keys.size() + " keys)");
            return nsk;
        }

        public static NamespacedKey getNamespacedKey(String key) {
            NamespacedKey nsk = keys.get(key);

            if (nsk == null) {
                nsk = createNamespacedKey(key);
            }

            return nsk;
        }

        public static <T, Z> ItemMeta setCustomData(ItemMeta meta, String key, PersistentDataType<T, Z> type, Z value) {
            NamespacedKey nsk = keys.get(key);

            if (nsk == null) {
                nsk = createNamespacedKey(key);
            }

            meta.getPersistentDataContainer().set(nsk, type, value);
            return meta;
        }

        public static <T, Z> void setCustomData(ItemStack item, String key, PersistentDataType<T, Z> type, Z value) {
            item.setItemMeta(setCustomData(item.getItemMeta(), key, type, value));
        }

        public static ItemMeta setCustomData(ItemMeta meta, String key, int value) {
            return setCustomData(meta, key, PersistentDataType.INTEGER, value);
        }

        public static void setCustomData(ItemStack item, String key, int value) {
            item.setItemMeta(setCustomData(item.getItemMeta(), key, value));
        }

        public static ItemMeta setCustomData(ItemMeta meta, String key, String value) {
            return setCustomData(meta, key, PersistentDataType.STRING, value);
        }

        public static void setCustomData(ItemStack item, String key, String value) {
            item.setItemMeta(setCustomData(item.getItemMeta(), key, value));
        }

        public static <T, Z> Z getCustomDataOrDefault(ItemMeta meta, String key, PersistentDataType<T, Z> type,
                Z defaultValue) {
            Z data = getCustomData(meta, key, type);
            if (data == null) {
                return defaultValue;
            }
            return data;
        }

        public static <T, Z> Z getCustomData(ItemMeta meta, String key, PersistentDataType<T, Z> type) {
            NamespacedKey nsk = keys.get(key);

            if (nsk == null) {
                nsk = createNamespacedKey(key);
            }

            return meta.getPersistentDataContainer().get(nsk, type);
        }
    }

}
