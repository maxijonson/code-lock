package io.github.maxijonson.commands;

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
}
