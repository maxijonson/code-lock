package io.github.maxijonson.commands;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;

import io.github.maxijonson.exceptions.CommandException;

/**
 * Should be implemented by commands that are executable by the server console
 */
public interface ServerCommand extends BaseCommand {

    /**
     * Executes the command that was executed by the server via the server console
     * 
     * @param server the server console
     * @param cmd    the issued command object
     * @param label  label of the command
     * @param args   additionnal arguments with the command ("/codelock <command>
     *               [args]")
     * @return whether or not the command completed successfully
     */
    boolean onCommand(ConsoleCommandSender server, Command cmd, String label, String[] args) throws CommandException;
}
