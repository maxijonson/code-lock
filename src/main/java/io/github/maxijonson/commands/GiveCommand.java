package io.github.maxijonson.commands;

import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import io.github.maxijonson.Utils;
import io.github.maxijonson.items.CodeLockItem;
import io.github.maxijonson.items.Item;

public class GiveCommand extends CodeLockCommand {
    private static GiveCommand instance = null;

    private GiveCommand() {
        super("give", "give [amount]");
    }

    @Override
    boolean onCommand(Player player, Command cmd, String label, String[] args) {
        int amount = 1;

        if (args.length > 0 && args[0] != null) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Color.RED + "The amount must be a number");
                return true;
            }
        }

        CodeLockItem codeLock = new CodeLockItem(amount);
        player.getInventory().addItem(codeLock);
        player.sendMessage(Integer.toString(amount) + " code lock given with ID '"
                + Utils.Meta.getCustomData(codeLock.getItemMeta(), Item.NSK_ID, PersistentDataType.STRING) + "'");

        return true;
    }

    public static GiveCommand getInstance() {
        if (instance == null) {
            instance = new GiveCommand();
        }
        return instance;
    }
}
