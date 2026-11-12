package fr.openmc.api.packetmenulib.menu;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.packetmenulib.events.VirtualInventoryClickEvent;
import fr.openmc.api.packetmenulib.events.VirtualInventoryCloseEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Voir <a href="https://minecraft.wiki/w/Java_Edition_protocol/Inventory#Windows">https://minecraft.wiki/w/Java_Edition_protocol/Inventory#Windows</a>
 */
public interface Menu {
    Component getTitle(OMCPlayer player);
    InventoryType getInventoryType();
    Map<Integer, ItemStack> getContent();
    void onInventoryClick(VirtualInventoryClickEvent inventoryClickEvent);
    void onInventoryClose(VirtualInventoryCloseEvent inventoryCloseEvent);
    boolean isCursorItemEnabled();
}
