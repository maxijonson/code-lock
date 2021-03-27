package io.github.maxijonson.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages every command available for the plugin. It delegates the commands to
 * the appropriate classes
 */
public class CommandManager implements CommandExecutor {

    private static ArrayList<CodeLockCommand> commands = new ArrayList<>();

    /**
     * Registers available commands
     */
    public static void init() {
        commands.add(GiveCommand.getInstance());
        commands.add(SaveCommand.getInstance());
        commands.add(LoadCommand.getInstance());
        commands.add(HelpCommand.getInstance());
    }

    /**
     * Listens to every commands and dispatches them to the appropriate class
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use that command!");
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        Player player = (Player) sender;
        String commandName = args[0];
        CodeLockCommand command = null;

        for (CodeLockCommand c : commands) {
            if (c.getName().equals(commandName)) {
                command = c;
            }
        }

        if (command != null) {
            if (!command.onCommand(player, cmd, label, Arrays.copyOfRange(args, 1, args.length))) {
                sender.sendMessage(ChatColor.RED + command.getName() + "usage: /codelock " + command.getUsage());
            }
            return true;
        }

        return false;
    }

    public static ArrayList<CodeLockCommand> getCommands() {
        return commands;
    }

}
