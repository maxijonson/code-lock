package io.github.maxijonson.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectOutputStream;

import io.github.maxijonson.CodeLock;
import io.github.maxijonson.Utils;

public class Data {
    private static Data instance = new Data();

    private HashMap<UUID, PlayerData> players = new HashMap<>();
    private HashMap<String, LockedBlock> blocks = new HashMap<>();

    private Data() {
    }

    public static Data getInstance() {
        return instance;
    }

    public boolean addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!players.containsKey(uuid)) {
            players.put(uuid, new PlayerData(uuid));
            return true;
        }
        return false;
    }

    public boolean addBlock(Block block) {
        String id = Utils.getBlockId(block);
        if (!blocks.containsKey(id)) {
            blocks.put(id, new LockedBlock(block));
            return true;
        }
        return false;
    }

    public boolean save() {
        try {
            File basePath = CodeLock.getDataFolderPath();

            for (PlayerData player : players.values()) {
                BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(
                        new FileOutputStream(basePath + File.separator + player.getUuid().toString())));
                out.writeObject(player);
                out.close();
            }
            return true;
        } catch (IOException e) {
            System.err.println("[CodeLock] ERROR: Unable to save data!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean load() {
        try {
            File basePath = CodeLock.getDataFolderPath();

            for (PlayerData player : players.values()) {
                BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(
                        new FileOutputStream(basePath + File.separator + player.getUuid().toString())));
                out.writeObject(player);
                out.close();
            }
            return true;
        } catch (IOException e) {
            System.err.println("[CodeLock] ERROR: Unable to save data!");
            e.printStackTrace();
            return false;
        }
    }
}
