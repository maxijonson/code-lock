package io.github.maxijonson.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.bukkit.entity.Player;

import io.github.maxijonson.Utils;
import io.github.maxijonson.exceptions.GuestCodeException;

/**
 * Represents a block in a world where the block has a code lock placed.
 */
public class LockedBlock extends DataEntity<LockedBlock> {
    private static transient final long serialVersionUID = 1L;
    public static transient final int CODE_LENGTH = 4;

    /**
     * Block types that could be valid by the default lockable block types criteria
     * but should be excluded from being lockable.
     */
    public static transient final ArrayList<Class<? extends BlockData>> BLOCKTYPE_BLACKLIST = new ArrayList<Class<? extends BlockData>>() {
        private static final long serialVersionUID = 8679858783883943919L;

        {
            add(Fence.class);
        }
    };

    /**
     * Block types that are not lockable by default but that should be.
     */
    public static transient final ArrayList<Class<? extends BlockData>> BLOCKTYPE_WHITELIST = new ArrayList<Class<? extends BlockData>>() {
        private static final long serialVersionUID = 1L;

        {
            // TODO: Add whitelisted block types as they are discovered
        }
    };

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
    private String masterCode = null;

    /** The assigned guest code on the block */
    private String guestCode = null;

    /** A list of authorized players */
    private ArrayList<UUID> masters = new ArrayList<>();

    /** A list of the authorized guest players */
    private ArrayList<UUID> guests = new ArrayList<>();

    /** The button material type that was used when placing the lock */
    private String buttonType = null;

    public LockedBlock(String world, String chunk, int x, int y, int z, String buttonType) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        updateId();

        this.chunk = chunk;
        this.buttonType = buttonType;

        // Shouldn't happen, but just in case
        if (buttonType == null) {
            this.buttonType = Material.OAK_BUTTON.name();
        }
    }

    public LockedBlock(World world, Chunk chunk, int x, int y, int z, String buttonType) {
        this(Utils.ID.getWorldId(world), Utils.ID.getChunkId(chunk), x, y, z, buttonType);
    }

    public LockedBlock(Block block, String buttonType) {
        this(block.getWorld(), block.getChunk(), block.getX(), block.getY(), block.getZ(), buttonType);
    }

    /**
     * Authorizes a player on the locked block, provided the right code
     * 
     * @param player The player to authorize
     * @param code   The given code by the player (to be compared with the block's
     *               code)
     * @return Whether the authorization was succesful or not
     */
    public boolean authorize(Player player, String code) {
        // First code entered or block is unlocked. Change code.
        if (this.masterCode == null || !locked) {
            setCode(code);
            return authorizeMaster(player);
        }

        // Try to authorize as a master
        if (code.equals(this.masterCode)) {
            authorizeMaster(player);
            return true;
        }

        // Try to authorize as a guest
        if (this.guestCode != null && code.equals(this.guestCode)) {
            authorizeGuest(player);
            return true;
        }

        // Wrong code
        return false;
    }

    /**
     * Authorizes a player on the locked block as a master, without a code
     * 
     * @param player
     * @return true (player will always be authorized)
     */
    public boolean authorizeMaster(Player player) {
        if (!masters.contains(player.getUniqueId())) {
            masters.add(player.getUniqueId());
        }
        return true;
    }

    /**
     * Authorizes a player on the locked block as a gues, without a code
     * 
     * @param player
     * @return true (player will always be authorized)
     */
    public boolean authorizeGuest(Player player) {
        if (!guests.contains(player.getUniqueId())) {
            guests.add(player.getUniqueId());
        }
        return true;
    }

    /**
     * Removes a player from the authorized list (master or guest)
     * 
     * @param player
     * @return Whether or not the player was found and deauthorized
     */
    public boolean deauthorize(Player player) {
        UUID playerId = player.getUniqueId();
        return masters.remove(playerId) || guests.remove(playerId);
    }

    /**
     * ONLY Checks if the player appears in the list of masters. You should use the
     * `canInteract` method to prevent or allow access.
     * 
     * @param player
     * @return Whether or not the player appears on the list of masters of the block
     */
    public boolean isMaster(Player player) {
        return masters.contains(player.getUniqueId());
    }

    /**
     * ONLY Checks if the player appears in the list of guests. You should use the
     * `canInteract` method to prevent or allow access.
     * 
     * @param player
     * @return Whether or not the player appears on the list of guests of the block
     */
    public boolean isGuest(Player player) {
        return guests.contains(player.getUniqueId());
    }

    /**
     * ONLY Checks if the player appears on any player list. You should use the
     * `canInteract` method to prevent or allow access.
     * 
     * @param player
     * @return Whether or not the player appears on the a list of the block
     */
    public boolean isAuthorized(Player player) {
        return isMaster(player) || isGuest(player);
    }

    /**
     * Checks if the player should be able to interact with the block normally.
     * 
     * @param player
     * @return Whether or not the player can interact with this block
     */
    public boolean canInteract(Player player) {
        return !locked || isAuthorized(player) || player.hasPermission("codelock.authorize.force");
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

    public String getCode() {
        return masterCode;
    }

    public String getButtonType() {
        return buttonType;
    }

    /**
     * Changes the code of the locked block, nullifies the guest code, locks it and
     * deauthorizes the list of authorized players
     */
    public void setCode(String code) {
        this.masterCode = code;
        this.guestCode = null;
        this.locked = true;
        this.masters.clear();
    }

    /**
     * Sets a guest code on the block, locks it and deauthorizes the list of guest
     * players.
     * 
     * @throws GuestCodeException if the guest code is the same as the master code
     */
    public void setGuestCode(String code) throws GuestCodeException {
        if (this.masterCode.equals(code)) {
            throw new GuestCodeException("The guest code may not be the same as the master code");
        }

        this.guestCode = code;
        this.locked = true;
        this.guests.clear();
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public static boolean isLockable(Block block) {
        Class<? extends BlockData> type = block.getBlockData().getClass();
        return (block.getType().isInteractable() || BLOCKTYPE_WHITELIST.contains(type))
                && !BLOCKTYPE_BLACKLIST.contains(type);
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

        if (masterCode != entity.masterCode) {
            masterCode = entity.masterCode;
        }

        if (masters.size() != entity.masters.size() || !masters.containsAll(entity.masters)) {
            masters.clear();
            masters.addAll(entity.masters);
        }
    }
}
