package io.github.maxijonson.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public abstract class CodeLockCommand {
    private String name;
    private String usage;

    public CodeLockCommand(String name, String usage) {
        this.name = name;
        this.usage = usage;
    }

    abstract boolean onCommand(Player player, Command cmd, String label, String[] args);

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }
}
