package io.github.maxijonson.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.maxijonson.exceptions.CommandException;
import io.github.maxijonson.items.CodeLockItem;

/**
 * Gives the player a codelock
 */
public class GiveCommand extends CodeLockCommand implements PlayerCommand, ServerCommand {
    private static GiveCommand instance = null;

    private GiveCommand() {
        super("give", "give [player] [amount]", "gives a code lock", "give <player> [amount]");
    }

    public static GiveCommand getInstance() {
        if (instance == null) {
            instance = new GiveCommand();
        }
        return instance;
    }

    private void giveCodeLock(Player player, int amount) {
        CodeLockItem codeLock = new CodeLockItem(amount);
        player.getInventory().addItem(codeLock);
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) throws CommandException {
        int amount = 1;
        Player receiver = player;

        // Either a player or an amount
        if (args.length == 1) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                receiver = Bukkit.getPlayer(args[0]);

                if (receiver == null) {
                    throw new CommandException("That player does not exist or isn't online");
                }
            }
        }

        // format should be: give player amount
        if (args.length == 2) {
            receiver = Bukkit.getPlayer(args[0]);

            if (receiver == null) {
                throw new CommandException("That player does not exist or isn't online");
            }

            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new CommandException("The amount must be a number");
            }
        }

        giveCodeLock(receiver, amount);
        return true;
    }

    @Override
    public boolean onCommand(ConsoleCommandSender server, Command cmd, String label, String[] args)
            throws CommandException {
        int amount = 1;
        Player receiver = null;

        if (args.length == 0) {
            throw new CommandException("Must specify the player");
        }
        if (args.length > 2) {
            throw new CommandException("Too many arguments");
        }

        receiver = Bukkit.getPlayer(args[0]);

        if (receiver == null) {
            throw new CommandException("That player does not exist or isn't online");
        }

        if (args.length == 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new CommandException("The amount must be a number");
            }
        }

        giveCodeLock(receiver, amount);
        return true;
    }

}
