package fr.openmc.api.packetmenulib;

import fr.openmc.api.packetmenulib.menu.Menu;
import fr.openmc.api.packetmenulib.utils.PacketUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PacketMenuLib {

    @Getter
    private static final Map<UUID, Menu> openMenus = new HashMap<>();
    @Getter
    private static final Map<UUID, Integer> windowIds = new HashMap<>();
    @Getter
    private static PacketListener packetListener;

    public static void init(JavaPlugin plugin) {
        packetListener = new PacketListener(plugin);
    }

    /**
     * Opens the specified menu for the given player and initializes the necessary packet communication.
     *
     * @param menu   the {@code Menu} instance to open for the player
     * @param player the {@code Player} who should see the menu
     */
    public static void openMenu(Menu menu, Player player) {
        int windowId = ((CraftPlayer) player).getHandle().nextContainerCounter();
        windowIds.put(player.getUniqueId(), windowId);
        PacketUtils.sendOpenInventoryPacket(player, windowId, menu.getInventoryType().getMenuType(), menu.getTitle());
        openMenus.put(player.getUniqueId(), menu);
        updateMenu(menu, player, 1);
    }

    /**
     * Closes the currently open menu for the specified player.
     *
     * @param player the player for whom the menu should be closed
     */
    public static void closeMenu(Player player) {
        Integer windowId = windowIds.get(player.getUniqueId());
        if (windowId != null) {
            PacketUtils.sendCloseInventoryPacket(player, windowId);
        }
        windowIds.remove(player.getUniqueId());
        openMenus.remove(player.getUniqueId());
        updateInv(player);
    }

    /**
     * Updates the menu for the specified player with the given state ID.
     *
     * @param menu    the Menu instance to update
     * @param player  the Player whose menu is being updated
     * @param stateId the state ID to be sent with the update
     */
    public static void updateMenu(Menu menu, Player player, Integer stateId) {
        UUID playerUUID = player.getUniqueId();
        Integer windowId = windowIds.get(playerUUID);

        if (windowId == null) {
            openMenus.remove(playerUUID);
            return;
        }
        ItemStack cursorItem = menu.isCursorItemEnabled() ? getCursorItem() : null;
        List<ItemStack> items = createItemList(menu);
        PacketUtils.sendContainerContentPacket(player, windowId, stateId, items, cursorItem);
    }

    /**
     * Updates the inventory of the specified player by retrieving their current inventory contents
     * and sending an updated packet with the inventory data.
     *
     * @param player the player whose inventory needs to be updated
     */
    public static void updateInv(Player player) {
        List<ItemStack> items = getPlayerItems(player);
        PacketUtils.sendContainerContentPacket(player, 0, 1, items, new ItemStack(Material.AIR));
    }

    /**
     * Creates and returns an invisible item
     *
     * @return An {@code ItemStack} representing the configured cursor item.
     */
    private static ItemStack getCursorItem() {
        ItemStack cursorItem = new ItemStack(Material.PAPER);
        ItemMeta cursorItemMeta = cursorItem.getItemMeta();
        assert cursorItemMeta != null;
        cursorItemMeta.setItemModel(NamespacedKey.minecraft("air"));
        cursorItemMeta.setHideTooltip(true);
        cursorItemMeta.setMaxStackSize(1);
        cursorItem.setItemMeta(cursorItemMeta);
        return cursorItem;
    }

    /**
     * Creates a list of {@code ItemStack} objects representing the inventory content of the given menu.
     *
     * @param menu the {@code Menu} instance whose inventory content is used to generate the list of {@code ItemStack} objects
     * @return a {@code List<ItemStack>} containing the items from the menu's inventory
     */
    private static List<ItemStack> createItemList(Menu menu) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < menu.getInventoryType().getSlots(); i++) {
            if (menu.getContent().containsKey(i)) {
                items.add(menu.getContent().get(i));
            } else {
                items.add(new ItemStack(Material.AIR));
            }
        }
        return items;
    }

    /**
     * Retrieves the player's inventory items and returns them as a list of {@code ItemStack}.
     *
     * @param player the player whose inventory items are to be retrieved
     * @return a list of {@code ItemStack} representing the player's inventory items
     */
    private static List<ItemStack> getPlayerItems(Player player) {
        List<ItemStack> items = new ArrayList<>(Collections.nCopies(45, new ItemStack(Material.AIR)));
        ItemStack[] contents = player.getInventory().getContents();
        // hotbar
        for (int i = 0; i < 9; i++) {
            items.set(i + 36, contents[i]);
        }
        // main inventory
        for (int i = 9; i < 36; i++) {
            items.set(i, contents[i]);
        }
        // armor
        items.set(5, contents[39]);
        items.set(6, contents[38]);
        items.set(7, contents[37]);
        items.set(8, contents[36]);
        return items;
    }
}
