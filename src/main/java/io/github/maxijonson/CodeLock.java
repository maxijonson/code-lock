package io.github.maxijonson;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.maxijonson.commands.CommandManager;
import io.github.maxijonson.data.Data;
import io.github.maxijonson.events.PlaceLockEvent;

/**
 * CodeLock A Minecraft plugin to add a code lock to various entities.
 * 
 * @author maxijonson
 * @version 1.0.0
 */
public class CodeLock extends JavaPlugin {
    private static CodeLock instance = null;
    private static File dataFolder = null;

    @Override
    public void onEnable() {
        // Get instance of plugin
        instance = this;

        // Prepare data folder
        dataFolder = getDataFolder();
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            dataFolder.mkdirs();
        }

        // Load Data TODO: enable the line below when ready
        Data.getInstance().load();

        // Init events
        getServer().getPluginManager().registerEvents(new PlaceLockEvent(), this);

        // Init commands
        CommandManager commandManager = CommandManager.getInstance();
        commandManager.init();
        this.getCommand("codelock").setExecutor(commandManager);
    }

    @Override
    public void onDisable() {
        // Save data TODO: enable the line below when ready
        Data.getInstance().save();
    }

    public static CodeLock getInstance() {
        return instance;
    }

    public static File getDataFolderPath() {
        return dataFolder;
    }
}
