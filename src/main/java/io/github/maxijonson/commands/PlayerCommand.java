package io.github.maxijonson.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.maxijonson.exceptions.CommandException;

/**
 * Should be implemented by commands that are executable by players
 */
public interface PlayerCommand extends BaseCommand {

    /**
     * Executes the command that was executed by an in-game player via the chat
     * 
     * @param player the player associated with the event
     * @param cmd    the issued command object
     * @param label  label of the command
     * @param args   additionnal arguments with the command ("/codelock <command>
     *               [args]")
     * @return whether or not the command completed successfully
     */
    boolean onCommand(Player player, Command cmd, String label, String[] args) throws CommandException;
}
