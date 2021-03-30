package io.github.maxijonson.events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.github.maxijonson.items.CodeLockItem;

public class PlayerJoinEvent implements Listener {

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public static void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (NamespacedKey key : CodeLockItem.buttons.keySet()) {
            player.discoverRecipe(key);
        }
    }
}
