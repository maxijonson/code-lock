package io.github.maxijonson.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.maxijonson.items.CodeLockItem;
import io.github.maxijonson.items.Item;

public class PlaceLockEvent implements Listener {
    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null
                && Item.matchId(event.getItem(), CodeLockItem.ID)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("The code lock cannot be placed on this entity.");
        }
    }
}
