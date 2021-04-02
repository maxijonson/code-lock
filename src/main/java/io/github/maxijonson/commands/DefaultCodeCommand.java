package io.github.maxijonson.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.maxijonson.data.Data;
import io.github.maxijonson.data.LockedBlock;
import io.github.maxijonson.data.PlayerData;
import io.github.maxijonson.exceptions.CommandException;
import net.md_5.bungee.api.ChatColor;

public class DefaultCodeCommand extends CodeLockCommand implements PlayerCommand {
    private static DefaultCodeCommand instance = null;

    public static DefaultCodeCommand getInstance() {
        if (instance == null) {
            instance = new DefaultCodeCommand();
        }
        return instance;
    }

    private DefaultCodeCommand() {
        super("default", "default <4-pin | unset>", "Sets or unsets a default code for code locks", "codelock.command.default");
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) throws CommandException {
        if (args.length != 1) {
            return false;
        }

        String code = args[0];
        PlayerData playerData = Data.getInstance().getPlayer(player.getUniqueId());

        if (code.equals("unset")) {
            playerData.setDefaultCode(null);
            player.sendMessage(ChatColor.GREEN + "Default code unset!");
            return true;
        }

        if (code.length() != LockedBlock.CODE_LENGTH) {
            player.sendMessage(
                    ChatColor.RED + String.format("The code must be a %d pin long code", LockedBlock.CODE_LENGTH));
            return true;
        }

        try {
            Integer.parseInt(code);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "The code must be a number");
            return true;
        }

        playerData.setDefaultCode(code);
        player.sendMessage(ChatColor.GREEN + "Default code set!");

        return true;
    }

}
