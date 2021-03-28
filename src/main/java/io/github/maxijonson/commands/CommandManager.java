package io.github.maxijonson.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.maxijonson.exceptions.CommandException;

/**
 * Manages every command available for the plugin. It delegates the commands to
 * the appropriate classes
 */
public class CommandManager implements CommandExecutor {
    private static CommandManager instance = null;
    private static ArrayList<PlayerCommand> playerCommands = new ArrayList<>();
    private static ArrayList<ServerCommand> serverCommands = new ArrayList<>();

    private CommandManager() {
    }

    private void registerCommand(CodeLockCommand command) {
        if (command instanceof PlayerCommand) {
            playerCommands.add((PlayerCommand) command);
        }
        if (command instanceof ServerCommand) {
            serverCommands.add((ServerCommand) command);
        }
    }

    /**
     * Registers available commands
     */
    public void init() {
        registerCommand(GiveCommand.getInstance());
        registerCommand(SaveCommand.getInstance());
        registerCommand(LoadCommand.getInstance());
        registerCommand(HelpCommand.getInstance());
    }

    /**
     * Listens to every commands and dispatches them to the appropriate class
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String commandName = args[0];
        BaseCommand command = null;
        boolean isPlayer = (sender instanceof Player);

        // Find the requested command
        for (BaseCommand c : isPlayer ? playerCommands : serverCommands) {
            if (c.getName().equals(commandName)) {
                command = c;
                break;
            }
        }

        // If found, execute it for the right sender
        if (command != null) {
            try {
                String[] trimmedArgs = Arrays.copyOfRange(args, 1, args.length);
                if (isPlayer && !((PlayerCommand) command).onCommand((Player) sender, cmd, label, trimmedArgs)) {
                    sender.sendMessage(
                            ChatColor.RED + command.getName() + "usage: /codelock " + command.getPlayerUsage());
                }
                if (!isPlayer && !((ServerCommand) command).onCommand((ConsoleCommandSender) sender, cmd, label,
                        trimmedArgs)) {
                    sender.sendMessage(
                            ChatColor.RED + command.getName() + "usage: /codelock " + command.getServerUsage());
                }
            } catch (CommandException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An unknown error occured");
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    public static ArrayList<PlayerCommand> getPlayerCommands() {
        return playerCommands;
    }

    public static ArrayList<ServerCommand> getServerCommands() {
        return serverCommands;
    }

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }
}
