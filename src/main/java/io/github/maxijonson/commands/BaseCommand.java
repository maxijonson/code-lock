package io.github.maxijonson.commands;

import org.bukkit.entity.Player;

/**
 * Defines methods that should be available on implemented entities
 */
public interface BaseCommand {
    /**
     * Getes the name of the command. The name is also used by the user to reference
     * it in the chat or server console.
     * 
     * @return The name of the command.
     */
    String getName();

    /**
     * Short description of what the command does
     * 
     * @return the description
     */
    String getDescription();

    /**
     * Usage of the command for a player, without the leading "/codelock"
     * 
     * @return the usage
     */
    String getPlayerUsage();

    /**
     * Usage of the command for a server, without the leading "/codelock"
     * 
     * @return the usage
     */
    String getServerUsage();

    /**
     * Whether or not the player is allowed to execute the command
     * 
     * @param player
     * @return
     */
    boolean hasPermission(Player player);
}
