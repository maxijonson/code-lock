package io.github.maxijonson.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.maxijonson.data.Data;

/**
 * Calls the `save` method on the Data instance
 */
public class SaveCommand extends CodeLockCommand implements PlayerCommand, ServerCommand {
    private static SaveCommand instance = null;

    private SaveCommand() {
        super("save", "save", "Saves the plugin data into the plugin's data folder");
    }

    private void save(CommandSender sender) {
        if (Data.getInstance().save()) {
            sender.sendMessage(ChatColor.GREEN + "Data saved!");
        } else {
            sender.sendMessage(ChatColor.RED + "Data could not be saved.");
        }
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Only OPs can use this command");
            return true;
        }
        save(player);
        return true;
    }

    @Override
    public boolean onCommand(ConsoleCommandSender server, Command cmd, String label, String[] args) {
        save(server);
        return true;
    }

    public static SaveCommand getInstance() {
        if (instance == null) {
            instance = new SaveCommand();
        }
        return instance;
    }

}
