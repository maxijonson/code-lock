package io.github.maxijonson.events;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

import io.github.maxijonson.Utils;
import io.github.maxijonson.data.Data;
import io.github.maxijonson.data.LockedBlock;

public class DamageBlockEvent implements Listener {

    public boolean processEvent(Block block, Player player) {
        // Initial Precondition
        if (!LockedBlock.isLockable(block)) {
            return false;
        }

        LockedBlock lockedBlock = Data.getInstance().getLockedBlock(block);

        // Not a LockedBlock. Do nothing.
        if (lockedBlock == null) {
            return false;
        }

        // Player is not authorized to destroy the block
        if (!lockedBlock.isAuthorized(player)) {
            player.sendMessage(ChatColor.RED + "You are not authorized to damage or destroy this locked block");
            return true;
        }

        Data.getInstance().removeBlock(lockedBlock.getWorld(), lockedBlock.getChunk(), lockedBlock.getId());
        return false;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockDestroy(BlockBreakEvent event) {
        boolean cancel = processEvent(Utils.Entity.getWorkableBlock(event.getBlock()), event.getPlayer());
        event.setCancelled(cancel);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockDamage(BlockDamageEvent event) {
        boolean cancel = processEvent(Utils.Entity.getWorkableBlock(event.getBlock()), event.getPlayer());
        event.setCancelled(cancel);
    }
}
