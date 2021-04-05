package io.github.maxijonson.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.maxijonson.Utils;
import io.github.maxijonson.data.Data;
import io.github.maxijonson.data.LockedBlock;
import net.md_5.bungee.api.ChatColor;

public class AuthorizeEvent implements Listener {

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public static void onBlockInteract(PlayerInteractEvent event) {
        // Initial Precondition
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = Utils.Entity.getWorkableBlock(event.getClickedBlock());

        // Not lockable, do nothing
        if (!LockedBlock.isLockable(block)) {
            return;
        }

        LockedBlock lockedBlock = Data.getInstance().getLockedBlock(block);

        // Not locked, do nothing
        if (lockedBlock == null) {
            return;
        }

        Player player = event.getPlayer();

        if (!lockedBlock.canInteract(player)) {
            event.setUseInteractedBlock(Result.DENY);
            if (!player.isSneaking()) {
                player.sendMessage(
                        ChatColor.RED + "Locked." + ChatColor.GOLD + " Sneak + Right click to enter the code.");
            }
        }
    }
}
