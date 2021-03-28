package io.github.maxijonson.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * For each available command, displays their name and description. Giving a
 * command name as argument gives the usage.
 */
public class HelpCommand extends CodeLockCommand implements PlayerCommand, ServerCommand {
    private static HelpCommand instance = null;

    private HelpCommand() {
        super("help", "help [command]", "Shows all available commands or a command's info");
    }

    private <T extends BaseCommand> void showHelp(CommandSender sender, ArrayList<T> commands, String[] args) {
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            BaseCommand command = null;

            for (BaseCommand c : commands) {
                if (c.getName().equals(search)) {
                    command = c;
                    break;
                }
            }

            if (command != null) {
                String usage = (sender instanceof Player) ? command.getPlayerUsage() : command.getServerUsage();
                sender.sendMessage(new String[] { ChatColor.YELLOW + command.getName(),
                        ChatColor.GRAY + command.getDescription(), ChatColor.AQUA + usage });
                return;
            }
        }

        ArrayList<String> msg = new ArrayList<>();

        for (BaseCommand command : commands) {
            msg.add(ChatColor.YELLOW + command.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY
                    + command.getDescription());
        }

        if (msg.size() > 0) {
            sender.sendMessage(msg.toArray(new String[msg.size()]));
        }
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) {
        showHelp(player, CommandManager.getPlayerCommands(), args);
        return true;
    }

    @Override
    public boolean onCommand(ConsoleCommandSender server, Command cmd, String label, String[] args) {
        showHelp(server, CommandManager.getServerCommands(), args);
        return true;
    }

    public static HelpCommand getInstance() {
        if (instance == null) {
            instance = new HelpCommand();
        }
        return instance;
    }
}
