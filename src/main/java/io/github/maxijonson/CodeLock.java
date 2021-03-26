package io.github.maxijonson;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.maxijonson.commands.CommandManager;
import io.github.maxijonson.events.PlaceLockEvent;

/**
 * CodeLock A Minecraft plugin to add a code lock to various entities.
 * 
 * @author maxijonson
 * @version 1.0.0
 */
public class CodeLock extends JavaPlugin {
    private static CodeLock instance = null;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("CodeLock is enabled");

        // Init events
        getServer().getPluginManager().registerEvents(new PlaceLockEvent(), this);

        // Init commands
        CommandManager.init();
        getCommand("codelock").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        getLogger().info("CodeLock is disabled");
    }

    public static CodeLock getInstance() {
        return instance;
    }
}
