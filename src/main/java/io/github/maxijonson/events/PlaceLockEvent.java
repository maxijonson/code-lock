package io.github.maxijonson.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.maxijonson.Utils;
import io.github.maxijonson.data.Data;
import io.github.maxijonson.data.LockedBlock;
import io.github.maxijonson.items.CodeLockItem;
import io.github.maxijonson.items.Item;

/**
 * Event fired when attempting to place a lock. The event will validate that the
 * targeted entity can be locked and will register the target as a LockedBlock,
 * if it can.
 */
public class PlaceLockEvent implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public static void onRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null && Item.matchId(item, CodeLockItem.ID)) {
            Block block = Utils.Entity.getWorkableBlock(event.getClickedBlock());
            Material type = block.getType();
            Player player = event.getPlayer();

            // Check if block is lockable
            if (!LockedBlock.isLockable(block)) {
                player.sendMessage(String.format("The code lock cannot be placed on this (%s)", type.toString()));
                event.setUseItemInHand(Result.DENY);
                return;
            }

            // Check if there isn't already a lock
            if (!Data.getInstance().addBlock(block)) {
                player.sendMessage("This block is already blocked!");
                return;
            }

            // Remove the item
            item.setAmount(item.getAmount() - 1);
            player.sendMessage("Code lock placed! Now set a code to lock it.");

            // Inform the player about CodeLock usage on first place
            if (Data.getInstance().addPlayer(player)) {
                player.sendMessage(new String[] { "Right click the locked block while sneaking to set the code",
                        "You can set a default code with " + ChatColor.AQUA + "/codelock default <4-pin>" });
            }

            // Cancel the interaction
            event.setUseInteractedBlock(Result.DENY);
        }
    }
}
