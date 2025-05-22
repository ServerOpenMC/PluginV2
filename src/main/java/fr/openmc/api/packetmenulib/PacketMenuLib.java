package fr.openmc.api.packetmenulib;

import fr.openmc.api.packetmenulib.menu.Menu;
import fr.openmc.api.packetmenulib.utils.PacketUtils;
import lombok.Getter;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    private static PacketMenuLib instance;
    @Getter
    private static PacketListener packetListener;

    public PacketMenuLib(JavaPlugin plugin) {
        instance = this;
        packetListener = new PacketListener(plugin);
    }

    public static void openMenu(Menu menu, Player player) {
        int windowId = PacketListener.getInstance().getLastWindowId().getOrDefault(player.getUniqueId(), 0) + 1;
        windowIds.put(player.getUniqueId(), windowId);
        PacketUtils.sendOpenInventoryPacket(player, windowId, menu.getInventoryType().getMenuType(), menu.getTitle());
        updateMenu(menu, player, 1);
        openMenus.put(player.getUniqueId(), menu);
    }

    public static void closeMenu(Player player) {
        PacketUtils.sendCloseInventoryPacket(player, windowIds.get(player.getUniqueId()));
        windowIds.remove(player.getUniqueId());
        openMenus.remove(player.getUniqueId());
        updateInv(player);
    }

    public static void updateMenu(Menu menu, Player player, Integer stateId) {
        ItemStack cursorItem = menu.isCursorItemEnabled() ? getCursorItem() : null;
        List<ItemStack> items = createItemList(menu);
        PacketUtils.sendContainerContentPacket(player, windowIds.get(player.getUniqueId()),stateId , items, cursorItem);
    }

    public static void updateInv(Player player) {
        List<ItemStack> items = getPlayerItems(player);
        PacketUtils.sendContainerContentPacket(player, 0,1 , items, new ItemStack(Material.AIR));
    }

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
