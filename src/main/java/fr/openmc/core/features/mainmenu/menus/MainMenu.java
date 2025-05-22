package fr.openmc.core.features.mainmenu.menus;

import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.api.packetmenulib.events.InventoryCloseEvent;
import fr.openmc.api.packetmenulib.menu.ClickType;
import fr.openmc.api.packetmenulib.menu.InventoryType;
import fr.openmc.api.packetmenulib.menu.Menu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MainMenu implements Menu {

    private final Component title;

    public MainMenu() {
        title = Component.text("MainMenu").color(NamedTextColor.BLUE);
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.GENERIC_9X6;
    }

    @Override
    public Map<Integer, ItemStack> getContent() {
        return Map.of();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.clickType() == ClickType.CLICK_OUTSIDE) {
            event.player().sendMessage(Component.text("Inventory Closed (click outside)"));
            PacketMenuLib.closeMenu(event.player());
        } else if (event.clickType() == ClickType.LEFT_CLICK) {
            event.player().sendMessage(Component.text("Left Click Slot: " + event.slot()));
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        event.player().sendMessage(Component.text("Inventory Closed (default)"));
    }

    @Override
    public boolean isCursorItemEnabled() {
        return false;
    }
}
