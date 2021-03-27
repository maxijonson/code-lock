package io.github.maxijonson.events;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.maxijonson.data.Data;
import io.github.maxijonson.items.CodeLockItem;
import io.github.maxijonson.items.Item;

/**
 * Event fired when attempting to place a lock. The event will validate that the
 * targeted entity can be locked and will register the target as a LockedBlock,
 * if it can.
 */
public class PlaceLockEvent implements Listener {

    /**
     * Materials that could be valid by the default lockable materials criteria but
     * should be excluded from being lockable.
     */
    private static final ArrayList<Material> MATERIAL_BLACKLIST = new ArrayList<Material>() {
        private static final long serialVersionUID = 1L;

        {
            add(Material.ACACIA_FENCE);
            add(Material.BIRCH_FENCE);
            add(Material.CRIMSON_FENCE);
            add(Material.DARK_OAK_FENCE);
            add(Material.JUNGLE_FENCE);
            add(Material.NETHER_BRICK_FENCE);
            add(Material.OAK_FENCE);
            add(Material.SPRUCE_FENCE);
            add(Material.WARPED_FENCE);
        }
    };

    /**
     * Materials that are not lockable by default but that should be.
     */
    private static final ArrayList<Material> MATERIAL_WHITELIST = new ArrayList<Material>() {
        private static final long serialVersionUID = 1L;

        {
            // TODO: Add whitelisted Materials as they are discovered
        }
    };

    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null
                && Item.matchId(event.getItem(), CodeLockItem.ID)) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            Material type = block.getType();

            // Only whitelisted or interactable blocks that have not been blacklisted can
            // have a code lock
            if ((!type.isInteractable() && !MATERIAL_WHITELIST.contains(type)) || MATERIAL_BLACKLIST.contains(type)) {
                player.sendMessage(String.format("The code lock cannot be placed on this (%s)", type.toString()));
                return;
            }

            if (!Data.getInstance().addBlock(event.getClickedBlock())) {
                player.sendMessage("This block is already blocked!");
                return;
            }
            player.getInventory().removeItem(event.getItem());
            player.sendMessage("Code lock placed!");

            if (!Data.getInstance().addPlayer(player)) {
                player.sendMessage(new String[] { "Right click the locked block to set the code",
                        "You can set a default code with " + ChatColor.AQUA + "/codelock default <4-pin>" });
            }
        }
    }
}
