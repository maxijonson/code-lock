package io.github.maxijonson.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import io.github.maxijonson.Utils;

/**
 * Represents a block in a world where the block has a code lock placed.
 */
public class LockedBlock extends DataEntity<LockedBlock> {
    private static transient final long serialVersionUID = 1L;

    /** Identifying attributes */
    private String world;
    private int x;
    private int y;
    private int z;
    private String id; // True identifying attribute, made of the above ones

    /**
     * The chunk where the block is. Note that this is not part of the identifying
     * attributes of a LockedBlock.
     */
    private String chunk;

    /**
     * Whether the block is locked or unlocked. When locked, only authorized players
     * can interact with the block
     */
    private boolean locked = false;

    /** The assigned code on the block */
    private int code = -1;

    /** A list of authorized players */
    private ArrayList<UUID> authorized = new ArrayList<>();

    public LockedBlock(String world, String chunk, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        updateId();

        this.chunk = chunk;
    }

    public LockedBlock(World world, Chunk chunk, int x, int y, int z) {
        this(world.getEnvironment().name(), Utils.ID.getChunkId(chunk), x, y, z);
    }

    public LockedBlock(Block block) {
        this(block.getWorld(), block.getChunk(), block.getX(), block.getY(), block.getZ());
    }

    public LockedBlock(World world, Chunk chunk, int x, int y, int z, boolean locked, int code) {
        this(world, chunk, x, y, z);
        this.locked = locked;
        this.code = code;
    }

    public LockedBlock(Block block, boolean locked, int code) {
        this(block.getWorld(), block.getChunk(), block.getX(), block.getY(), block.getZ(), locked, code);
    }

    /**
     * Authorizes a player on the locked block, provided the right code
     * 
     * @param player The player to authorize
     * @param code   The given code by the player (to be compared with the block's
     *               code)
     * @return Whether the authorization was succesful or not
     */
    public boolean authorize(Player player, int code) {
        if (code == this.code) {
            authorized.add(player.getUniqueId());
            return true;
        }

        return false;
    }

    /**
     * Updates the block ID based on its positionnal attributes. Shoud be called
     * everytime the world, x, y or z gets updated (which should not happen)
     */
    public void updateId() {
        id = Utils.ID.getLockedBlockId(this);
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getId() {
        return id;
    }

    public String getChunk() {
        return chunk;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void sync(LockedBlock entity) {
        if (id != entity.id || chunk != entity.chunk) {
            world = entity.world;
            x = entity.x;
            y = entity.y;
            z = entity.z;
            updateId();

            chunk = entity.chunk;

            Utils.Log.warn(String.format(
                    "Locked block at (%d, %d, %d) in world %s had its position changed. This should not have happened. Its position was reverted back to the loaded block's position, but you should check the block is still there.",
                    x, y, z, world));
        }

        if (locked != entity.locked) {
            locked = entity.locked;
        }

        if (code != entity.code) {
            code = entity.code;
        }

        if (authorized.size() != entity.authorized.size() || !authorized.containsAll(entity.authorized)) {
            authorized.clear();
            authorized.addAll(entity.authorized);
        }
    }
}
