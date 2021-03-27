package io.github.maxijonson.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * For each available command, displays their name and description. Giving a
 * command name as argument gives the usage.
 */
public class HelpCommand extends CodeLockCommand {
    private static HelpCommand instance = null;

    private HelpCommand() {
        super("help", "help [command]", "Shows all available commands or a command's info");
    }

    @Override
    boolean onCommand(Player player, Command cmd, String label, String[] args) {
        ArrayList<CodeLockCommand> commands = CommandManager.getCommands();

        if (args.length == 1) {
            String search = args[0].toLowerCase();
            CodeLockCommand command = null;

            for (CodeLockCommand c : commands) {
                if (c.getName().equals(search)) {
                    command = c;
                    break;
                }
            }

            if (command != null) {
                player.sendMessage(new String[] { ChatColor.YELLOW + command.getName(),
                        ChatColor.GRAY + command.getDescription(), ChatColor.AQUA + command.getUsage() });
                return true;
            }
        }

        ArrayList<String> msg = new ArrayList<>();

        for (CodeLockCommand command : commands) {
            msg.add(ChatColor.YELLOW + command.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY
                    + command.getDescription());
        }
        if (msg.size() > 0) {
            player.sendMessage(msg.toArray(new String[msg.size()]));
        }

        return true;
    }

    public static HelpCommand getInstance() {
        if (instance == null) {
            instance = new HelpCommand();
        }
        return instance;
    }
}
