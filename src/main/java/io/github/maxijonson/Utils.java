package io.github.maxijonson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.maxijonson.data.LockedBlock;

public class Utils {

    public static class Entity {
        /**
         * Useful for entities that span over more than 1 block. This method will return
         * the same block wherever the player interacts with it.
         * 
         * @see Bisected
         * @see Chest
         * @param block
         * @return
         */
        public static Block getWorkableBlock(Block block) {
            Block b = block;
            if (block.getBlockData() instanceof Door) {
                Door door = (Door) b.getBlockData();
                if (door.getHalf() == Half.TOP) {
                    b = b.getRelative(BlockFace.DOWN);
                }
            }
            if (block.getBlockData() instanceof Chest) {
                Chest chest = (Chest) b.getBlockData();
                System.out.println(chest.getFacing());

                if (chest.getType() == Chest.Type.RIGHT) {
                    BlockFace face = chest.getFacing();
                    switch (face) {
                    case NORTH:
                        b = b.getRelative(BlockFace.WEST);
                        break;
                    case WEST:
                        b = b.getRelative(BlockFace.SOUTH);
                        break;
                    case SOUTH:
                        b = b.getRelative(BlockFace.EAST);
                        break;
                    case EAST:
                        b = b.getRelative(BlockFace.NORTH);
                        break;
                    default:
                        break;
                    }
                }
            }
            return b;
        }
    }

    public static class FS {
        /**
         * Retrieves a File directory given a `path` or creates it if it does not exist.
         * 
         * @param first the path
         * @param more  additionnal paths
         * @return The File
         * @throws IOException if the directory cannot be created or the File exists but
         *                     is not a directory
         */
        public static File getOrCreateDir(String first, String... more) throws IOException {
            Path path = Paths.get(first, more);
            File file = new File(path.toString());
            if (!file.exists() && !file.mkdirs()) {
                throw new IOException("Unable to create the " + path + " directory");
            }
            if (!file.isDirectory()) {
                throw new IOException(file + " is not a directory");
            }
            return file;
        }

        /**
         * Retrieves a File given a `path` or creates it if it does not exist.
         * 
         * @param first the path
         * @param more  additionnal paths
         * @return The File
         * @throws IOException if the file cannot be created or the File exists but is
         *                     not a file
         */
        public static File getOrCreateFile(String first, String... more) throws IOException {
            Path path = Paths.get(first, more);
            File file = new File(path.toString());
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Unable to create the " + path + " file");
            }
            if (!file.isFile()) {
                throw new IOException(file + " is not a file");
            }
            return file;
        }
    }

    public static class ID {
        public static final String TYPE_LOCKEDBLOCK = "lockedblock";
        public static final String TYPE_CHUNK = "chunk";

        /**
         * Creates an ID for a position in a world. This ID should theorically be
         * unique.
         */
        public static String createPositionalId(String world, int x, int y, int z, String type) {
            return String.format("t_%s w_%s x_%d y_%d z_%d", type, world, x, y, z);
        }

        public static String createPositionalId(World world, int x, int y, int z, String type) {
            return createPositionalId(getWorldId(world), x, y, z, type);
        }

        public static String getLockedBlockId(LockedBlock block) {
            return createPositionalId(block.getWorld(), block.getX(), block.getY(), block.getZ(), TYPE_LOCKEDBLOCK);
        }

        public static String getLockedBlockId(Block block) {
            return createPositionalId(block.getWorld(), block.getX(), block.getY(), block.getZ(), TYPE_LOCKEDBLOCK);
        }

        public static String getChunkId(Chunk chunk) {
            return createPositionalId(chunk.getWorld(), chunk.getX(), 0, chunk.getZ(), TYPE_CHUNK);
        }

        public static String getWorldId(World world) {
            return world.getEnvironment().name();
        }
    }

    public static class Log {
        public static final String PREFIX = "[CodeLock] ";
        public static ConsoleCommandSender console = Bukkit.getConsoleSender();

        public static void info(String msg) {
            console.sendMessage(PREFIX + msg);
        }

        public static void warn(String msg) {
            console.sendMessage(ChatColor.YELLOW + PREFIX + "WARN: " + msg);
        }

        public static void err(String msg) {
            console.sendMessage(ChatColor.RED + PREFIX + "ERROR: " + msg);
        }
    }

    public static class Meta {
        /**
         * Collection of created keys during the plugins lifetime (cleared when the
         * server stops). Used to accelerate key lookup and reusability instead of
         * always regenerating one
         */
        private static HashMap<String, NamespacedKey> keys = new HashMap<>();

        /**
         * Creates a NamespacedKey with the `key` string as entry key. This
         * NamespacedKey can then be used on ItemMeta to define custom fields.
         * 
         * @param key the name of the NamespacedKey
         * @return the NamespacedKey
         */
        public static NamespacedKey createNamespacedKey(String key) {
            NamespacedKey nsk = new NamespacedKey(CodeLock.getInstance(), key);
            keys.put(key, nsk);
            Utils.Log.info("Created new key " + key + " (now with " + keys.size() + " keys)");
            return nsk;
        }

        /**
         * Gets a previously created NamespacedKey. Creates a new one if it does not
         * exist.
         * 
         * @param key the key name
         * @return the NamespacedKey associated with that name (never null)
         */
        public static NamespacedKey getNamespacedKey(String key) {
            NamespacedKey nsk = keys.get(key);

            if (nsk == null) {
                nsk = createNamespacedKey(key);
            }

            return nsk;
        }

        /**
         * Adds a custom field with a value on the provided `meta` object
         * 
         * @param <T>   T: The primary object type that is stored in the given tag
         * @param <Z>   Z: The retrieved object type when applying this tag type
         * @param meta  The meta object to add the custom field and data on
         * @param key   The name of the field
         * @param type  The type of data that will be added as value
         * @param value The value of the field (typed based on what was provided for
         *              `type`)
         * @return The modified ItemMeta. Note that the passed `meta` param is modified
         *         in place.
         */
        public static <T, Z> ItemMeta setCustomData(ItemMeta meta, String key, PersistentDataType<T, Z> type, Z value) {
            NamespacedKey nsk = getNamespacedKey(key);

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

        /**
         * Gets a custom field on a `meta` object. If it isn't present, returns the
         * `defaultValue` instead
         * 
         * @param <T>          T: The primary object type that is stored in the given
         *                     tag
         * @param <Z>          Z: The retrieved object type when applying this tag type
         * @param meta         The meta object to add the custom field and data on
         * @param key          The name of the field
         * @param type         The type of data that will be added as value
         * @param defaultValue The value to return if none is found (typed based on what
         *                     was provided for `type`)
         * @return The value of the field, typed based on what was provided for `type`
         */
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
