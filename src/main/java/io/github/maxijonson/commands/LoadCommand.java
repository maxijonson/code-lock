package io.github.maxijonson.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.maxijonson.data.Data;

/**
 * Calls the `load` method of the Data instance
 */
public class LoadCommand extends CodeLockCommand implements PlayerCommand, ServerCommand {
    private static LoadCommand instance = null;

    private LoadCommand() {
        super("load", "load", "Loads the plugin data (overwrites the current data)", "codelock.command.load");
    }

    private void load(CommandSender sender) {
        if (Data.getInstance().load()) {
            sender.sendMessage(ChatColor.GREEN + "Data loaded!");
        } else {
            sender.sendMessage(ChatColor.RED + "Data could not be loaded.");
        }
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) {
        load(player);
        return true;
    }

    @Override
    public boolean onCommand(ConsoleCommandSender server, Command cmd, String label, String[] args) {
        load(server);
        return true;
    }

    public static LoadCommand getInstance() {
        if (instance == null) {
            instance = new LoadCommand();
        }
        return instance;
    }
}
