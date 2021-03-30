package io.github.maxijonson.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.maxijonson.Utils;
import io.github.maxijonson.data.Data;
import io.github.maxijonson.data.LockedBlock;
import io.github.maxijonson.items.CodeLockItem;
import net.md_5.bungee.api.ChatColor;

public class GUIClickEvent implements Listener {
    public static final Material MENUITEM_KEYPADSEQUENCE_FILLED = Material.GREEN_STAINED_GLASS_PANE;

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public static void onInventoryItemClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        if (view.getTitle().equals(OpenGUIEvent.GUI_TITLE) && event.getWhoClicked() instanceof Player) {
            event.setResult(Result.DENY);

            // Don't register double clicks
            if (event.getClick() == ClickType.DOUBLE_CLICK) {
                return;
            }

            int slot = event.getSlot();
            ItemStack selected = event.getCurrentItem();

            // Nothing selected or ItemStack selected (does nothing but hold metadata)
            if (selected == null || slot < 0 || slot == OpenGUIEvent.GUI_METAITEM_POS) {
                return;
            }

            Inventory inventory = event.getInventory();
            ItemStack[] contents = inventory.getContents();
            Player player = (Player) event.getWhoClicked();

            if (selected.getType() == OpenGUIEvent.MENUITEM_KEYPADINPUT) { // Keypad Input
                keypadInput(selected, contents, player);
            } else if (selected.getType() == OpenGUIEvent.MENUITEM_CLEAR) { // Clear Input
                clearInput(contents);
            } else if (selected.getType() == OpenGUIEvent.MENUITEM_LOCK) { // Lock
                setLocked(contents, true, player);
            } else if (selected.getType() == OpenGUIEvent.MENUITEM_UNLOCK) { // Unlock
                setLocked(contents, false, player);
            } else if (selected.getType() == OpenGUIEvent.MENUITEM_REMOVE) { // Remove
                remove(contents, player);
            } else if (selected.getType() == OpenGUIEvent.MENUITEM_DEAUTHORIZE) { // Deauthorize
                deauthorize(contents, player);
            }

        }
    }

    private static String getBlockWorld(ItemStack[] contents) {
        return Utils.Meta.getCustomData(contents[OpenGUIEvent.GUI_METAITEM_POS].getItemMeta(),
                OpenGUIEvent.NSK_BLOCKWORLD, PersistentDataType.STRING);
    }

    private static String getBlockChunk(ItemStack[] contents) {
        return Utils.Meta.getCustomData(contents[OpenGUIEvent.GUI_METAITEM_POS].getItemMeta(),
                OpenGUIEvent.NSK_BLOCKCHUNK, PersistentDataType.STRING);
    }

    private static String getBlockId(ItemStack[] contents) {
        return Utils.Meta.getCustomData(contents[OpenGUIEvent.GUI_METAITEM_POS].getItemMeta(), OpenGUIEvent.NSK_BLOCKID,
                PersistentDataType.STRING);
    }

    private static LockedBlock getLockedBlock(ItemStack[] contents) {
        return Data.getInstance().getLockedBlock(getBlockWorld(contents), getBlockChunk(contents),
                getBlockId(contents));
    }

    private static void keypadInput(ItemStack selected, ItemStack[] contents, Player player) {
        try {
            // Get which key was selected
            String input = selected.getItemMeta().getDisplayName();

            // Find the first empty sequence block and replace it with a filled block
            String current = "";
            for (int i = 0; i < LockedBlock.CODE_LENGTH; ++i) {
                ItemStack item = contents[OpenGUIEvent.GUI_SEQUENCE_POS + i];
                if (item == null) {
                    continue;
                }
                if (item.getType() == MENUITEM_KEYPADSEQUENCE_FILLED) {
                    current += item.getItemMeta().getDisplayName();
                }
                if (item.getType() == OpenGUIEvent.MENUITEM_KEYPADSEQUENCE_EMPTY) {
                    item.setType(MENUITEM_KEYPADSEQUENCE_FILLED);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(input);
                    item.setItemMeta(meta);
                    current += input;
                    break;
                }
            }

            // If complete, authorize on the locked block
            if (current.length() == LockedBlock.CODE_LENGTH) {
                LockedBlock lockedBlock = getLockedBlock(contents);

                if (lockedBlock.authorize(player, current)) {
                    player.sendMessage(ChatColor.GREEN + "You are now authorized!");
                } else {
                    player.sendMessage(ChatColor.RED + "Wrong code!");
                    player.damage(5);
                }

                player.closeInventory();
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Something went wrong...");
            e.printStackTrace();
        }
    }

    private static void clearInput(ItemStack[] contents) {
        for (int i = 0; i < LockedBlock.CODE_LENGTH; ++i) {
            ItemStack item = contents[OpenGUIEvent.GUI_SEQUENCE_POS + i];
            if (item == null) {
                continue;
            }
            if (item.getType() == MENUITEM_KEYPADSEQUENCE_FILLED) {
                item.setType(OpenGUIEvent.MENUITEM_KEYPADSEQUENCE_EMPTY);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(OpenGUIEvent.MENUITEM_KEYPADSEQUENCE_EMPTY_TXT);
                item.setItemMeta(meta);
            }
        }
    }

    private static void setLocked(ItemStack[] contents, boolean locked, Player player) {
        LockedBlock lockedBlock = getLockedBlock(contents);
        lockedBlock.setLocked(locked);
        player.sendMessage(ChatColor.AQUA + "The entity is now " + ChatColor.GOLD + (locked ? "locked" : "unlocked"));
        player.closeInventory();
    }

    private static void remove(ItemStack[] contents, Player player) {
        Data.getInstance().removeBlock(getBlockWorld(contents), getBlockChunk(contents), getBlockId(contents));
        player.getInventory().addItem(new CodeLockItem());
        player.sendMessage(ChatColor.AQUA + "Lock removed");
        player.closeInventory();
    }

    private static void deauthorize(ItemStack[] contents, Player player) {
        LockedBlock lockedBlock = getLockedBlock(contents);
        lockedBlock.deauthorize(player);
        player.sendMessage(ChatColor.AQUA + "You were deauthorized from this locked entity");
        player.closeInventory();
    }
}
