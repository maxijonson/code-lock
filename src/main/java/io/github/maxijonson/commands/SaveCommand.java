package io.github.maxijonson.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.maxijonson.data.Data;

/**
 * Calls the `save` method on the Data instance
 */
public class SaveCommand extends CodeLockCommand {
    private static SaveCommand instance = null;

    private SaveCommand() {
        super("save", "save", "Saves the plugin data into the plugin's data folder");
    }

    @Override
    boolean onCommand(Player player, Command cmd, String label, String[] args) {
        if (Data.getInstance().save()) {
            player.sendMessage(ChatColor.GREEN + "Data saved!");
        } else {
            player.sendMessage(ChatColor.RED + "Data could not be saved.");
        }
        return true;
    }

    public static SaveCommand getInstance() {
        if (instance == null) {
            instance = new SaveCommand();
        }
        return instance;
    }

}
