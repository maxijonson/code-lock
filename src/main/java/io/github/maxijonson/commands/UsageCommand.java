package io.github.maxijonson.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.maxijonson.exceptions.CommandException;

public class UsageCommand extends CodeLockCommand implements PlayerCommand {
    private static UsageCommand instance = null;

    public static final HashMap<String, Topic> topics = new HashMap<String, Topic>() {
        private static final long serialVersionUID = 1L;

        {
            Topic place = new Topic("place", "Place a code lock on an entity",
                    new String[] { "Equip code lock in hand", "Look at a lockable entity",
                            "Right click to place the code lock. (Skip next steps if you set a default code)",
                            "Sneak + Right click to open the code GUI",
                            "Set the code (entity will lock automatically)" });
            put(place.name, place);

            Topic authorize = new Topic("authorize", "Authorize yourself on a locked entity", new String[] {
                    "Look at locked entity", "Sneak + Right click to open the code lock GUI", "Enter the code" });
            put(authorize.name, authorize);

            Topic unlock = new Topic("unlock", "Unlock an entity (used to change the code)",
                    new String[] { "Look at locked entity (on which you are authorized)",
                            "Sneak + Right click to open the code lock GUI", "Click on the icon labeled 'unlock'" });
            put(unlock.name, unlock);

            Topic change = new Topic("change", "Change the code of a code lock",
                    new String[] { "Make sure you unlock the code lock first (see 'usage unlock')",
                            "Sneak + Right click to open the code lock GUI",
                            "Set the code (entity will lock automatically)" });
            put(change.name, change);

        }
    };

    public static UsageCommand getInstance() {
        if (instance == null) {
            instance = new UsageCommand();
        }
        return instance;
    }

    private UsageCommand() {
        super("usage", "usage [topic]", "Shows instructions for the CodeLock");
    }

    @Override
    public boolean onCommand(Player player, Command cmd, String label, String[] args) throws CommandException {
        if (args.length != 1) {
            ArrayList<String> msgs = new ArrayList<>();
            for (Topic topic : topics.values()) {
                msgs.add(String.format("%s%s%s: %s%s", ChatColor.YELLOW, topic.name, ChatColor.RESET, ChatColor.GRAY,
                        topic.description));
            }
            player.sendMessage((String[]) msgs.toArray());
            return true;
        }
        String topicName = args[0];
        Topic topic = topics.get(topicName);

        if (topic == null) {
            player.sendMessage(ChatColor.RED + "That topic does not exist");
            return true;
        }

        ArrayList<String> msgs = new ArrayList<>();

        for (int i = 0; i < topic.steps.length; ++i) {
            msgs.add(String.format("%s%d: %s%s", ChatColor.AQUA, i, ChatColor.RESET, topic.steps[i]));
        }

        player.sendMessage((String[]) msgs.toArray());

        return true;
    }

    private static class Topic {
        String name;
        String description;
        String[] steps;

        public Topic(String name, String description, String[] steps) {
            this.name = name;
            this.description = description;
            this.steps = steps;
        }
    }

}