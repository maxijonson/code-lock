package io.github.maxijonson.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import io.github.maxijonson.CodeLock;
import io.github.maxijonson.Utils;

/**
 * Holds arbitrary data that should be persisted. Has the ability to save and
 * load this data.
 */
@SuppressWarnings("unchecked")
public class Data {
    private static final String PATH_BASE = "data";
    private static final String PATH_PLAYERS = Paths.get(PATH_BASE, "players").toString();
    private static final String PATH_LOCKEDBLOCKS = Paths.get(PATH_BASE, "locked_blocks").toString();

    private static Data instance = new Data();

    /** List of player configs */
    private HashMap<UUID, PlayerData> players = new HashMap<>();

    /**
     * List of all blocks with a code lock, per chunk, per world. The type can be
     * read as the following:
     * 
     * HashMap<WORLD_NAME, HashMap<CHUNK_ID, HashMap<BLOCK_ID, LockedBlock>>>
     * 
     * where WORLD_NAME is the Utils.ID.getWorldId(World), CHUNK_ID is the result of
     * Utils.ID.getChunkId(Chunk) and BLOCK_ID is the result of
     * Utils.ID.getLockedBlockId(LockedBlock)
     */
    private HashMap<String, HashMap<String, HashMap<String, LockedBlock>>> blocks = new HashMap<>();

    private Data() {
    }

    public static Data getInstance() {
        return instance;
    }

    /**
     * Adds a PlayerData to the Data players list.
     * 
     * @param playerData The PlayerData object
     * @return whether the player was added (new) or not (existing)
     */
    public boolean addPlayer(PlayerData playerData) {
        PlayerData existing = players.get(playerData.getUuid());
        if (existing == null) {
            players.put(playerData.getUuid(), playerData);
            return true;
        }
        return false;
    }

    /**
     * Adds a PlayerData to the Data players list
     * 
     * @param player The player's UUID
     * @return whether the player was added (new) or not (existing)
     */
    public boolean addPlayer(UUID uuid) {
        return addPlayer(new PlayerData(uuid));
    }

    /**
     * Adds a PlayerData to the Data players list
     * 
     * @param player The player (Bukkit) to create the PlayerData from
     * @return whether the player was added (new) or not (existing)
     */
    public boolean addPlayer(Player player) {
        return addPlayer(player.getUniqueId());
    }

    public PlayerData getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public PlayerData getPlayerOrCreate(UUID uuid) {
        PlayerData player = players.get(uuid);
        if (player == null) {
            addPlayer(uuid);
            player = players.get(uuid);
        }
        return player;
    }

    /**
     * Clears the players list
     */
    public boolean clearPlayers() {
        try {
            File basePath = Utils.FS.getOrCreateDir(CodeLock.getDataFolderPath().toString());
            File playersDir = Utils.FS.getOrCreateDir(basePath.toString(), PATH_PLAYERS);
            if (!deleteDirectory(playersDir)) {
                throw new Exception("Couldn't delete the players directory");
            }
            players.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a locked block to the Data blocks list.
     * 
     * @param lockedBlock The LockedBlock to add
     * @return whether the block was added (new) or not (existing)
     */
    public boolean addBlock(LockedBlock lockedBlock) {
        // This method could be done in fewer lines, but I'm trying to minimize the
        // amount of operations to do just what needs to be done without unnecessary
        // checks or lookups. It is not as DRY as I would like it to be, but should
        // accomplish this performance objective. The amount of operations should be the
        // same for any case of the `block`'s' "existance" (i.e: the world doesn't exist
        // in the list, the chunk doesn't exist in the world, the block doesn't exist in
        // the chunk or the block exists)

        // Check if the world exists in the list. If not, add the block from there
        HashMap<String, HashMap<String, LockedBlock>> blockWorld = blocks.get(lockedBlock.getWorld());
        if (blockWorld == null) {
            HashMap<String, LockedBlock> chunkBlocks = new HashMap<>(); // Create the chunk block list
            chunkBlocks.put(lockedBlock.getId(), lockedBlock); // Add the block to the chunk
            HashMap<String, HashMap<String, LockedBlock>> worldChunks = new HashMap<>(); // Create the world chunk list
            worldChunks.put(lockedBlock.getChunk(), chunkBlocks); // Add the chunk to the world
            blocks.put(lockedBlock.getWorld(), worldChunks); // add the world to the blocks list
            return true;
        }

        // Check if the chunk exists in the world. If not, add the block from there
        HashMap<String, LockedBlock> blockChunk = blockWorld.get(lockedBlock.getChunk());
        if (blockChunk == null) {
            HashMap<String, LockedBlock> chunkBlocks = new HashMap<>(); // Create the chunk block list
            chunkBlocks.put(lockedBlock.getId(), lockedBlock); // Add the block to the chunk
            blockWorld.put(lockedBlock.getChunk(), chunkBlocks); // Add the chunk to the world
            return true;
        }

        // Check if the block exists in the chunk. If not, add the block from there
        LockedBlock chunkBlock = blockChunk.get(lockedBlock.getId());
        if (chunkBlock == null) {
            blockChunk.put(lockedBlock.getId(), lockedBlock); // Add the block to the chunk
            return true;
        }

        return false;
    }

    /**
     * Converts a Block to a LockedBlock and adds it to the Data blocks list
     * 
     * @param block The Block (Bukkit) to add
     * @return whether the block was added (new) or not (existing)
     */
    public boolean addBlock(Block block, String buttonType) {
        return addBlock(new LockedBlock(block, buttonType));
    }

    public LockedBlock removeBlock(String world, String chunk, String id) {
        LockedBlock lockedBlock = getLockedBlock(world, chunk, id);

        if (lockedBlock == null) {
            return null;
        }

        return blocks.get(world).get(chunk).remove(id);
    }

    /**
     * Clears the blocks list
     */
    public boolean clearBlocks() {
        try {
            File basePath = Utils.FS.getOrCreateDir(CodeLock.getDataFolderPath().toString());
            File lockedBlocksDir = Utils.FS.getOrCreateDir(basePath.toString(), PATH_LOCKEDBLOCKS);
            if (!deleteDirectory(lockedBlocksDir)) {
                throw new Exception("Couldn't delete the locked blocks directory");
            }
            blocks.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the LockedBlock associated with the passed block, if any.
     * 
     * @param block The block from which the LockedBlock should be retrieved
     * @return The associated LockedBlock or `null` if none is found
     */
    public LockedBlock getLockedBlock(Block block) {
        HashMap<String, HashMap<String, LockedBlock>> world = blocks.get(Utils.ID.getWorldId(block.getWorld()));
        if (world == null) {
            return null;
        }

        HashMap<String, LockedBlock> chunk = world.get(Utils.ID.getChunkId(block.getChunk()));
        if (chunk == null) {
            return null;
        }

        return chunk.get(Utils.ID.getLockedBlockId(block));
    }

    public LockedBlock getLockedBlock(String world, String chunk, String id) {
        HashMap<String, HashMap<String, LockedBlock>> w = blocks.get(world);
        if (w == null) {
            return null;
        }

        HashMap<String, LockedBlock> c = w.get(chunk);
        if (c == null) {
            return null;
        }

        return c.get(id);
    }

    /**
     * Saves the Data into the plugin's data folder. It creates a file for every
     * player and a file per world, per chunk for every locked block.
     * 
     * @return
     */
    public boolean save() {
        try {
            File basePath = Utils.FS.getOrCreateDir(CodeLock.getDataFolderPath().toString());
            File playersDir = Utils.FS.getOrCreateDir(basePath.toString(), PATH_PLAYERS);
            File lockedBlocksDir = Utils.FS.getOrCreateDir(basePath.toString(), PATH_LOCKEDBLOCKS);

            // Save players
            for (PlayerData player : players.values()) {
                BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                        Utils.FS.getOrCreateFile(playersDir.toString(), player.getUuid().toString()))));
                out.writeObject(player);
                out.close();
            }

            // Save each world chunks with locked blocks
            for (String world : blocks.keySet()) {
                File worldDir = Utils.FS.getOrCreateDir(lockedBlocksDir.toString(), world);

                HashMap<String, HashMap<String, LockedBlock>> chunks = blocks.get(world);

                for (String chunkId : chunks.keySet()) {
                    HashMap<String, LockedBlock> chunkBlocks = chunks.get(chunkId);

                    BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(
                            new FileOutputStream(Utils.FS.getOrCreateFile(worldDir.toString(), chunkId))));
                    out.writeObject(chunkBlocks);
                    out.close();
                }
            }

            return true;
        } catch (IOException e) {
            Utils.Log.err("Unable to save data!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads players and blocks into their respective static Data fields. For each
     * entity, if they already exist for some reason, sync their attributes with the
     * loaded ones.
     * 
     * @return whether the operation was a success or not
     */
    public boolean load() {
        try {
            File basePath = Utils.FS.getOrCreateDir(CodeLock.getDataFolderPath().toString());
            File playersDir = Utils.FS.getOrCreateDir(basePath.toString(), PATH_PLAYERS);
            File lockedBlocksDir = Utils.FS.getOrCreateDir(basePath.toString(), PATH_LOCKEDBLOCKS);

            // Load players
            File[] playerFiles = playersDir.listFiles();
            for (File playerFile : playerFiles) {
                try {
                    if (!playerFile.isFile()) {
                        Utils.Log.warn(String.format(
                                "%s is not a player file. Make sure only valid files are stored in the CodeLock data directory!",
                                playerFile.getAbsolutePath()));
                        continue;
                    }

                    BukkitObjectInputStream in = new BukkitObjectInputStream(
                            new GZIPInputStream(new FileInputStream(playerFile)));
                    PlayerData loadedPlayer = (PlayerData) in.readObject();

                    addPlayer(loadedPlayer);

                    in.close();
                } catch (Exception e) {
                    Utils.Log.err(String.format("Could not load player for file: %s", playerFile.getAbsolutePath()));
                    e.printStackTrace();
                }
            }

            // Load locked blocks
            File[] worldDirs = lockedBlocksDir.listFiles();
            for (File worldDir : worldDirs) {
                try {
                    if (!worldDir.isDirectory()) {
                        Utils.Log.warn(String.format(
                                "%s is not a world directory. Make sure only valid files are stored in the CodeLock data directory!",
                                worldDir.getAbsolutePath()));
                        continue;
                    }

                    String world = worldDir.getName();

                    File[] chunkFiles = worldDir.listFiles();

                    for (File chunkFile : chunkFiles) {
                        try {
                            if (!chunkFile.isFile()) {
                                Utils.Log.warn(String.format(
                                        "%s is not a chunk file. Make sure only valid files are stored in the CodeLock data directory!",
                                        chunkFile.getAbsolutePath()));
                                continue;
                            }

                            String chunkId = chunkFile.getName();

                            BukkitObjectInputStream in = new BukkitObjectInputStream(
                                    new GZIPInputStream(new FileInputStream(chunkFile)));
                            HashMap<String, LockedBlock> lockedBlocks = (HashMap<String, LockedBlock>) in.readObject();
                            in.close();

                            for (LockedBlock lockedBlock : lockedBlocks.values()) {
                                try {
                                    addBlock(lockedBlock);
                                } catch (Exception e) {
                                    Utils.Log.err(
                                            String.format("Could not load LockedBlock [%s] in world [%s] chunk [%d]",
                                                    lockedBlock.getId(), world, chunkId));
                                }
                            }

                        } catch (Exception e) {
                            Utils.Log.err(String.format("Could not load chunk in world [%s] for file: %s", world,
                                    chunkFile.getAbsolutePath()));
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Utils.Log.err(String.format("Could not load world for directory: %s", worldDir.getAbsolutePath()));
                    e.printStackTrace();
                }
            }

            return true;
        } catch (Exception e) {
            Utils.Log.err("Could not load Data!");
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteDirectory(File dir) throws Exception {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }

        return dir.delete();
    }
}
