package fr.openmc.api.packetmenulib.menu;

import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.api.packetmenulib.events.InventoryCloseEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface Menu {
    Component getTitle();
    InventoryType getInventoryType();
    Map<Integer, ItemStack> getContent();
    void onInventoryClick(InventoryClickEvent inventoryClickEvent);
    void onInventoryClose(InventoryCloseEvent inventoryCloseEvent);
    boolean isCursorItemEnabled();
}
