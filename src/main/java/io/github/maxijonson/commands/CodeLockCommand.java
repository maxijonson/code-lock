package io.github.maxijonson.commands;

/**
 * Base class for a command
 */
public abstract class CodeLockCommand implements BaseCommand {
    private String name;
    private String playerUsage;
    private String serverUsage;
    private String description;

    public CodeLockCommand(String name, String usage, String description, String serverUsage) {
        this.name = name;
        this.playerUsage = usage;
        this.serverUsage = serverUsage;
        this.description = description;
    }

    public CodeLockCommand(String name, String usage, String description) {
        this(name, usage, description, usage);
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
}
