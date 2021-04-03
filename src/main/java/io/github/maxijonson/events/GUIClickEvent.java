package io.github.maxijonson.events;

import org.bukkit.ChatColor;
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
import io.github.maxijonson.exceptions.GuestCodeException;
import io.github.maxijonson.items.CodeLockItem;

public class GUIClickEvent implements Listener {
    public static final Material MENUITEM_KEYPADSEQUENCE_FILLED = Material.GREEN_STAINED_GLASS_PANE;

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public static void onInventoryItemClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        // Initial Precondition
        if (!view.getTitle().equals(OpenGUIEvent.GUI_TITLE) || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        event.setResult(Result.DENY);

        // Don't register double clicks (prevents triple events) and shift left clicks
        // (prevents retrieving the buttons in the player's inventory in some cases)
        if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.SHIFT_LEFT) {
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
        Material selectedType = selected.getType();

        // FIXME: This is starting to look like a giant 'else if' hell. Introduce Chain
        // of responsability pattern?
        if (selectedType == OpenGUIEvent.MENUITEM_KEYPADINPUT
                || selectedType == OpenGUIEvent.MENUITEM_GUESTKEYPADINPUT) { // Keypad Input
            keypadInput(selected, contents, player, selectedType == OpenGUIEvent.MENUITEM_GUESTKEYPADINPUT);
        } else if (selectedType == OpenGUIEvent.MENUITEM_CLEAR) { // Clear Input
            clearInput(contents);
        } else if (selectedType == OpenGUIEvent.MENUITEM_LOCK) { // Lock
            setLocked(contents, true, player);
        } else if (selectedType == OpenGUIEvent.MENUITEM_UNLOCK) { // Unlock
            setLocked(contents, false, player);
        } else if (selectedType == OpenGUIEvent.MENUITEM_REMOVE) { // Remove
            remove(contents, player);
        } else if (selectedType == OpenGUIEvent.MENUITEM_DEAUTHORIZE) { // Deauthorize
            deauthorize(contents, player);
        } else if (selectedType == OpenGUIEvent.MENUITEM_FORCEAUTHORIZE) { // Force Authorize
            forceAuthorize(contents, player);
        } else if (selectedType == OpenGUIEvent.MENUITEM_MASTERMODE
                || selectedType == OpenGUIEvent.MENUITEM_GUESTMODE) { // Switch mode
            switchMode(contents, selectedType == OpenGUIEvent.MENUITEM_GUESTMODE);
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

    private static void keypadInput(ItemStack selected, ItemStack[] contents, Player player, boolean isGuest) {
        try {
            // Get which key was selected
            String input = selected.getItemMeta().getDisplayName();

            // Find the first empty sequence block and replace it with a filled block
            String code = "";
            for (int i = 0; i < LockedBlock.CODE_LENGTH; ++i) {
                ItemStack item = contents[OpenGUIEvent.GUI_SEQUENCE_POS + i];
                if (item == null) {
                    continue;
                }
                if (item.getType() == MENUITEM_KEYPADSEQUENCE_FILLED) {
                    code += item.getItemMeta().getDisplayName();
                }
                if (item.getType() == OpenGUIEvent.MENUITEM_KEYPADSEQUENCE_EMPTY) {
                    item.setType(MENUITEM_KEYPADSEQUENCE_FILLED);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(input);
                    item.setItemMeta(meta);
                    code += input;
                    break;
                }
            }

            // If complete, authorize on the locked block
            if (code.length() == LockedBlock.CODE_LENGTH) {
                LockedBlock lockedBlock = getLockedBlock(contents);

                if (isGuest) { // Setting a new guest code
                    try {
                        lockedBlock.setGuestCode(code);
                        player.sendMessage(ChatColor.GREEN + "Guest code set!");
                    } catch (GuestCodeException e) {
                        player.sendMessage(ChatColor.RED + e.getMessage());
                    }
                } else if (lockedBlock.authorize(player, code)) { // Authorizing (or setting) the master code
                    player.sendMessage(ChatColor.GREEN + "You are now authorized!");
                } else { // Wrong code
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
        LockedBlock lockedBlock = getLockedBlock(contents);
        Data.getInstance().removeBlock(lockedBlock.getWorld(), lockedBlock.getChunk(), lockedBlock.getId());
        player.getInventory().addItem(new CodeLockItem(lockedBlock.getButtonType()));
        player.sendMessage(ChatColor.AQUA + "Lock removed");
        player.closeInventory();
    }

    private static void deauthorize(ItemStack[] contents, Player player) {
        LockedBlock lockedBlock = getLockedBlock(contents);
        lockedBlock.deauthorize(player);
        player.sendMessage(ChatColor.AQUA + "You were deauthorized from this locked entity");
        player.closeInventory();
    }

    private static void forceAuthorize(ItemStack[] contents, Player player) {
        LockedBlock lockedBlock = getLockedBlock(contents);
        lockedBlock.authorizeMaster(player);
        player.sendMessage(ChatColor.GREEN + "You are now authorized!");
        player.closeInventory();
    }

    private static void switchMode(ItemStack[] contents, boolean toGuestMode) {
        Material from = toGuestMode ? OpenGUIEvent.MENUITEM_KEYPADINPUT : OpenGUIEvent.MENUITEM_GUESTKEYPADINPUT;
        Material to = toGuestMode ? OpenGUIEvent.MENUITEM_GUESTKEYPADINPUT : OpenGUIEvent.MENUITEM_KEYPADINPUT;
        for (int i = OpenGUIEvent.GUI_KEYPAD_POS; i <= OpenGUIEvent.GUI_KEYPADZERO_POS; i++) {
            if (contents[i] == null || contents[i].getType() != from) {
                continue;
            }

            contents[i].setType(to);
        }
        ItemStack modeButton = contents[OpenGUIEvent.GUI_MODE_POS];
        ItemMeta modeButtonMeta = modeButton.getItemMeta();
        modeButton.setType(toGuestMode ? OpenGUIEvent.MENUITEM_MASTERMODE : OpenGUIEvent.MENUITEM_GUESTMODE);
        modeButtonMeta.setDisplayName(
                toGuestMode ? OpenGUIEvent.MENUITEM_MASTERMODE_TXT : OpenGUIEvent.MENUITEM_GUESTMODE_TXT);
        modeButton.setItemMeta(modeButtonMeta);
    }
}
