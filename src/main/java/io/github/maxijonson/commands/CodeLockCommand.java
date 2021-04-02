package io.github.maxijonson.commands;

import org.bukkit.entity.Player;

/**
 * Base class for a command
 */
public abstract class CodeLockCommand implements BaseCommand {
    private String name;
    private String playerUsage;
    private String serverUsage;
    private String description;
    private String permission;

    public CodeLockCommand(String name, String usage, String description, String permission, String serverUsage) {
        this.name = name;
        this.playerUsage = usage;
        this.serverUsage = serverUsage;
        this.description = description;
        this.permission = permission;
    }

    public CodeLockCommand(String name, String usage, String description, String permission) {
        this(name, usage, description, permission, usage);
    }

    public String getName() {
        return name;
    }

    public String getPlayerUsage() {
        return playerUsage;
    }

    public String getServerUsage() {
        return serverUsage;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }
}
