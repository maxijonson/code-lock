package io.github.maxijonson.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.maxijonson.data.Data;

/**
 * Calls the `load` method of the Data instance
 */
public class LoadCommand extends CodeLockCommand {
    private static LoadCommand instance = null;

    private LoadCommand() {
        super("load", "load", "Loads the plugin data (overwrites the current data)");
    }

    @Override
    boolean onCommand(Player player, Command cmd, String label, String[] args) {
        if (Data.getInstance().load()) {
            player.sendMessage(ChatColor.GREEN + "Data loaded!");
        } else {
            player.sendMessage(ChatColor.RED + "Data could not be loaded.");
        }
        return true;
    }

    public static LoadCommand getInstance() {
        if (instance == null) {
            instance = new LoadCommand();
        }
        return instance;
    }
}
