package io.github.maxijonson.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Base class for a command
 */
public abstract class CodeLockCommand {
    /**
     * Name of the command (/codelock <name>)
     */
    private String name;

    /**
     * Usage for the command, excluding the leading "/codelock"
     */
    private String usage;

    /**
     * Informative description on what the command does
     */
    private String description;

    public CodeLockCommand(String name, String usage, String description) {
        this.name = name;
        this.usage = usage;
        this.description = description;
    }

    /**
     * Executes the command
     * 
     * @param player the player associated with the event
     * @param cmd    the issued command object
     * @param label  label of the command
     * @param args   additionnal arguments with the command ("/codelock <command>
     *               [args]")
     * @return
     */
    abstract boolean onCommand(Player player, Command cmd, String label, String[] args);

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }
}
