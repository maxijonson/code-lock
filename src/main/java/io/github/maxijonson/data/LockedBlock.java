package io.github.maxijonson.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LockedBlock implements Serializable {
    private static transient final long serialVersionUID = 1L;

    private World world;
    private int x;
    private int y;
    private int z;
    private boolean locked = false;
    private int code = -1;
    private ArrayList<UUID> authorized = new ArrayList<>();

    public LockedBlock(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LockedBlock(Block block) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public LockedBlock(World world, int x, int y, int z, boolean locked, int code) {
        this(world, x, y, z);
        this.locked = locked;
        this.code = code;
    }

    public LockedBlock(Block block, boolean locked, int code) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ(), locked, code);
    }

    public boolean authorize(Player player, int code) {
        if (code == this.code) {
            authorized.add(player.getUniqueId());
            return true;
        }

        return false;
    }

    public World getWorld() {
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
}
