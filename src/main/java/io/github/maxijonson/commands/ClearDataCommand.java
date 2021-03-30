package io.github.maxijonson.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.maxijonson.data.Data;
import io.github.maxijonson.exceptions.CommandException;
import net.md_5.bungee.api.ChatColor;

/**
 * Calls the `clear` method of the Data instance
 */
public class ClearDataCommand extends CodeLockCommand implements PlayerCommand, ServerCommand {
    private static final String TYPE_ALL = "all";
    private static final String TYPE_PLAYERS = "players";
    private static final String TYPE_BLOCKS = "blocks";

    private static ClearDataCommand instance = null;
    private static boolean warned = false;

    private ClearDataCommand() {
        super("cleardata", String.format("cleardata [%s | %s]", TYPE_PLAYERS, TYPE_BLOCKS),
                "Clears all or specified data in memory");
    }

    public static ClearDataCommand getInstance() {
        if (instance == null) {
            instance = new ClearDataCommand();
        }
        return instance;
    }

    private boolean onCommand(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new CommandException("Too many arguments");
        }
        String list = TYPE_ALL;

        if (!warned) {
            sender.sendMessage(ChatColor.YELLOW
                    + "This will clear the current players and/or locked blocks data (only created and used by the CodeLock plugin). This action is not reversible (backup data folder?). If you wish to continue, relaunch the command.");
            warned = true;
            return true;
        }

        if (args.length == 1) {
            list = args[0];

            if (!list.equals(TYPE_PLAYERS) && !list.equals(TYPE_BLOCKS)) {
                throw new CommandException(String.format("Argument must be '%s' or '%s'", TYPE_PLAYERS, TYPE_BLOCKS));
            }
        }

        if (list.equals(TYPE_PLAYERS) || list.equals(TYPE_ALL)) {
            if (!Data.getInstance().clearPlayers()) {
                throw new CommandException("Players could not be cleared");
            }
            sender.sendMessage(ChatColor.GREEN + TYPE_PLAYERS + " cleared.");
        }

        if (list.equals(TYPE_BLOCKS) || list.equals(TYPE_ALL)) {
            if (!Data.getInstance().clearBlocks()) {
                throw new CommandException("Locked blocks could not be cleared");
            }
            sender.sendMessage(ChatColor.GREEN + TYPE_BLOCKS + " cleared.");
        }

        warned = false;
        return true;
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) throws CommandException {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Only OPs can use this command");
            return true;
        }
        return onCommand(player, args);
    }

    @Override
    public boolean onCommand(ConsoleCommandSender server, Command cmd, String label, String[] args)
            throws CommandException {
        return onCommand(server, args);
    }

}
