package io.github.maxijonson.events;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.maxijonson.Utils;
import io.github.maxijonson.data.Data;
import io.github.maxijonson.data.LockedBlock;
import net.md_5.bungee.api.ChatColor;

public class OpenGUIEvent implements Listener {
    // Metdata
    public static final String ID = "openguievent";
    public static final String NSK_BLOCKWORLD = ID + ".block.world";
    public static final String NSK_BLOCKCHUNK = ID + ".block.chunk";
    public static final String NSK_BLOCKID = ID + ".block.id";

    // Button icons
    public static final Material MENUITEM_KEYPADINPUT = Material.BLACK_STAINED_GLASS_PANE;
    public static final Material MENUITEM_KEYPADSEQUENCE_EMPTY = Material.BROWN_STAINED_GLASS_PANE;
    public static final Material MENUITEM_CLEAR = Material.BLUE_STAINED_GLASS_PANE;
    public static final Material MENUITEM_LOCK = Material.RED_STAINED_GLASS_PANE;
    public static final Material MENUITEM_UNLOCK = Material.LIME_STAINED_GLASS_PANE;
    public static final Material MENUITEM_REMOVE = Material.REDSTONE;
    public static final Material MENUITEM_DEAUTHORIZE = Material.OAK_DOOR;
    public static final Material MENUITEM_FORCEAUTHORIZE = Material.IRON_DOOR;
    public static final Material MENUITEM_GUESTKEYPADINPUT = Material.ORANGE_STAINED_GLASS_PANE;
    public static final Material MENUITEM_GUESTMODE = Material.SPRUCE_DOOR;
    public static final Material MENUITEM_MASTERMODE = Material.DARK_OAK_DOOR;

    // Button labels
    public static final String GUI_TITLE = ChatColor.DARK_AQUA + "Code Lock";
    public static final String MENUITEM_KEYPADSEQUENCE_EMPTY_TXT = "Empty";
    public static final String MENUITEM_CLEAR_TXT = "Clear";
    public static final String MENUITEM_LOCK_TXT = "Lock";
    public static final String MENUITEM_UNLOCK_TXT = "Unlock";
    public static final String MENUITEM_REMOVE_TXT = "Remove Lock";
    public static final String MENUITEM_DEAUTHORIZE_TXT = "Deauthorize Yourself";
    public static final String MENUITEM_FORCEAUTHORIZE_TXT = "Force Authorization";
    public static final String MENUITEM_GUESTMODE_TXT = "Change Guest Code";
    public static final String MENUITEM_MASTERMODE_TXT = "Change Master Code";

    // Button positions
    public static final int GUI_ROWSIZE = 9;
    public static final int GUI_ROWS = 5;
    public static final int GUI_SIZE = GUI_ROWSIZE * GUI_ROWS;
    public static final int GUI_SEQUENCE_POS = 3;
    public static final int GUI_KEYPAD_POS = GUI_SEQUENCE_POS + GUI_ROWSIZE;
    public static final int GUI_KEYPADZERO_POS = GUI_KEYPAD_POS + 3 * GUI_ROWSIZE;
    public static final int GUI_CLEAR_POS = GUI_KEYPADZERO_POS + 1;
    public static final int GUI_MODE_POS = GUI_CLEAR_POS + 1;
    public static final int GUI_METAITEM_POS = GUI_ROWSIZE - 1;
    public static final int GUI_LOCK_POS = 15;
    public static final int GUI_REMOVE_POS = GUI_LOCK_POS + GUI_ROWSIZE;
    public static final int GUI_DEAUTHORIZE_POS = GUI_REMOVE_POS + GUI_ROWSIZE;
    public static final int GUI_FORCEAUTHORIZE_POS = GUI_DEAUTHORIZE_POS + GUI_ROWSIZE;

    // ItemStacks
    public static final ItemStack[] GUI_KEYPAD = new ItemStack[GUI_SIZE];
    public static final ItemStack GUI_LOCK = new ItemStack(MENUITEM_LOCK);
    public static final ItemStack GUI_UNLOCK = new ItemStack(MENUITEM_UNLOCK);
    public static final ItemStack GUI_REMOVE = new ItemStack(MENUITEM_REMOVE);
    public static final ItemStack GUI_DEAUTHORIZE = new ItemStack(MENUITEM_DEAUTHORIZE);
    public static final ItemStack GUI_FORCEAUTHORIZE = new ItemStack(MENUITEM_FORCEAUTHORIZE);
    public static final ItemStack GUI_GUESTMODE = new ItemStack(MENUITEM_GUESTMODE);

    static {
        // Input sequence
        for (int i = 0; i < LockedBlock.CODE_LENGTH; i++) {
            ItemStack input = new ItemStack(MENUITEM_KEYPADSEQUENCE_EMPTY);
            ItemMeta meta = input.getItemMeta();
            meta.setDisplayName(MENUITEM_KEYPADSEQUENCE_EMPTY_TXT);
            input.setItemMeta(meta);
            GUI_KEYPAD[GUI_SEQUENCE_POS + i] = input;
        }

        // Keypad
        for (int i = 9; i > 0; --i) {
            ItemStack input = new ItemStack(MENUITEM_KEYPADINPUT, i);
            ItemMeta meta = input.getItemMeta();
            meta.setDisplayName(Integer.toString(i));
            input.setItemMeta(meta);
            int row = ((GUI_ROWSIZE - i) / 3) * GUI_ROWSIZE;
            int col = (i + 2) % 3;
            GUI_KEYPAD[GUI_KEYPAD_POS + row + col] = input;
        }

        // '0' input
        ItemStack inputZero = new ItemStack(MENUITEM_KEYPADINPUT);
        ItemMeta metaZero = inputZero.getItemMeta();
        metaZero.setDisplayName("0");
        inputZero.setItemMeta(metaZero);
        GUI_KEYPAD[GUI_KEYPADZERO_POS] = inputZero;

        // Clear button
        ItemStack inputClear = new ItemStack(MENUITEM_CLEAR);
        ItemMeta metaClear = inputClear.getItemMeta();
        metaClear.setDisplayName(MENUITEM_CLEAR_TXT);
        inputClear.setItemMeta(metaClear);
        GUI_KEYPAD[GUI_CLEAR_POS] = inputClear;

        // Lock button
        ItemMeta metaLock = GUI_LOCK.getItemMeta();
        metaLock.setDisplayName(MENUITEM_LOCK_TXT);
        GUI_LOCK.setItemMeta(metaLock);

        // Unlock button
        ItemMeta metaUnlock = GUI_UNLOCK.getItemMeta();
        metaUnlock.setDisplayName(MENUITEM_UNLOCK_TXT);
        GUI_UNLOCK.setItemMeta(metaUnlock);

        // Remove button
        ItemMeta metaRemove = GUI_REMOVE.getItemMeta();
        metaRemove.setDisplayName(MENUITEM_REMOVE_TXT);
        GUI_REMOVE.setItemMeta(metaRemove);

        // Deauthorize button
        ItemMeta metaDeauthorize = GUI_DEAUTHORIZE.getItemMeta();
        metaDeauthorize.setDisplayName(MENUITEM_DEAUTHORIZE_TXT);
        GUI_DEAUTHORIZE.setItemMeta(metaDeauthorize);

        // Force authorize button
        ItemMeta metaForceAuthorize = GUI_FORCEAUTHORIZE.getItemMeta();
        metaForceAuthorize.setDisplayName(MENUITEM_FORCEAUTHORIZE_TXT);
        GUI_FORCEAUTHORIZE.setItemMeta(metaForceAuthorize);

        // Guest mode button
        ItemMeta metaGuestMode = GUI_GUESTMODE.getItemMeta();
        metaGuestMode.setDisplayName(MENUITEM_GUESTMODE_TXT);
        GUI_GUESTMODE.setItemMeta(metaGuestMode);
    }

    /**
     * Adds the `item` to the `guiItems` at the specified `index`
     * 
     * @param guiItems
     * @param item
     * @param index
     */
    public static void addItem(ItemStack[] guiItems, ItemStack item, int index) {
        guiItems[index] = item;
    }

    /**
     * Adds `items` to the `guiItems` by replacing `null` indexes of the `guiItems`
     * with the corresponding non `null` index of the `items`, if any. Kind of a
     * merge, but the size never changes
     * 
     * @param guiItems The array on which to apply the `items`. This array will be
     *                 modified in place.
     * @param items    The items that will be applied on the `guiItems`
     */
    public static void addItems(ItemStack[] guiItems, ItemStack[] items) {
        if (guiItems.length != items.length) {
            throw new InvalidParameterException("guiItems and items must be the same size");
        }
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null || guiItems[i] != null) {
                continue;
            }
            addItem(guiItems, item, i);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public static void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Initial Precondition
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !player.isSneaking()) {
            return;
        }

        Block block = Utils.Entity.getWorkableBlock(event.getClickedBlock());

        // Not lockable, do nothing
        if (!LockedBlock.isLockable(block)) {
            return;
        }

        LockedBlock lockedBlock = Data.getInstance().getLockedBlock(block);

        // Not locked, do nothing
        if (lockedBlock == null) {
            return;
        }

        boolean isMaster = lockedBlock.isMaster(player);
        boolean isGuest = !isMaster && lockedBlock.isGuest(player);

        // Create an ItemStack of the same type of the LockedBlock with the ID of the
        // LockedBlock for future reference
        ItemStack metaItem = new ItemStack(block.getType());
        Utils.Meta.setCustomData(metaItem, NSK_BLOCKWORLD, PersistentDataType.STRING, lockedBlock.getWorld());
        Utils.Meta.setCustomData(metaItem, NSK_BLOCKCHUNK, PersistentDataType.STRING, lockedBlock.getChunk());
        Utils.Meta.setCustomData(metaItem, NSK_BLOCKID, PersistentDataType.STRING, lockedBlock.getId());
        ItemMeta itemMeta = metaItem.getItemMeta();
        List<String> itemLore = new ArrayList<String>();
        itemLore.add(lockedBlock.isLocked() ? ChatColor.RED + "Locked" : ChatColor.GREEN + "Unlocked");
        if (isMaster) {
            itemLore.add(ChatColor.GREEN + "Authorized");
        } else if (isGuest) {
            itemLore.add(ChatColor.BLUE + "Guest");
        } else {
            itemLore.add(ChatColor.RED + "Unauthorized");
        }
        itemMeta.setLore(itemLore);
        metaItem.setItemMeta(itemMeta);

        Inventory gui = Bukkit.createInventory(player, GUI_SIZE, GUI_TITLE);
        ItemStack[] guiItems = new ItemStack[GUI_SIZE];

        // ItemStack that holds the meta
        addItem(guiItems, metaItem, GUI_METAITEM_POS);

        // When the block is locked
        if (lockedBlock.isLocked()) {
            // Player is master
            if (isMaster) {
                // Unlock button
                addItem(guiItems, GUI_UNLOCK, GUI_LOCK_POS);

                // Deauthorize button
                addItem(guiItems, GUI_DEAUTHORIZE, GUI_DEAUTHORIZE_POS);
            } else if (isGuest) {
                // Deauthorize button
                addItem(guiItems, GUI_DEAUTHORIZE, GUI_DEAUTHORIZE_POS);
            } else { // Player is unauthorized
                // Keypad
                addItems(guiItems, GUI_KEYPAD);

                // Force authorize
                if (player.hasPermission("codelock.authorize.guioption")) {
                    addItem(guiItems, GUI_FORCEAUTHORIZE, GUI_FORCEAUTHORIZE_POS);
                }
            }
        } else { // Block is unlocked
            // Player is authorized
            if (isMaster) {
                // Lock button
                addItem(guiItems, GUI_LOCK, GUI_LOCK_POS);

                // Deauthorize button
                addItem(guiItems, GUI_DEAUTHORIZE, GUI_DEAUTHORIZE_POS);

                // Guest Mode button
                addItem(guiItems, GUI_GUESTMODE, GUI_MODE_POS);
            } else if (isGuest) {
                // Deauthorize button
                addItem(guiItems, GUI_DEAUTHORIZE, GUI_DEAUTHORIZE_POS);
            } else {
                // Force authorize (without changing the code for others or removing the lock)
                if (lockedBlock.getCode() != null && player.hasPermission("codelock.authorize.guioption")) {
                    addItem(guiItems, GUI_FORCEAUTHORIZE, GUI_FORCEAUTHORIZE_POS);
                }
            }

            // Keypad
            addItems(guiItems, GUI_KEYPAD);

            // Remove button
            addItem(guiItems, GUI_REMOVE, GUI_REMOVE_POS);
        }

        gui.setContents(guiItems);
        player.openInventory(gui);
        event.setUseInteractedBlock(Result.DENY);
    }

}
