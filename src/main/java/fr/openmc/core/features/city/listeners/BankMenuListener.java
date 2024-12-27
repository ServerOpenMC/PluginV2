package fr.openmc.core.features.city.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.BankMenu;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class BankMenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) { return; }

        City city = CityManager.getPlayerCity(player.getUniqueId()); // Ca permets de charger les villes en background
        if (city == null) { return; }

        BankMenu menu = city.getBankMenu();
        Inventory inv = event.getInventory();

        if (menu == null) { return; }
        if (inv != menu.getInventory()) { return; }

        // L'inventaire est la banque de ville, on peut *enfin* faire qqchose

        if (event.getSlot() == 49) { // Close Button
            exit(city, inv, menu);
            player.closeInventory();
            return;
        }

        if (event.getSlot() == 45 && menu.hasPreviousPage()) { // Previous Button
            city.setBankMenu(new BankMenu(city, menu.getPage() - 1));
            city.getBankMenu().open(player);
            return;
        }

        if (event.getSlot() == 53 && menu.hasNextPage()) { // Next Button
            city.setBankMenu(new BankMenu(city, menu.getPage() + 1));
            city.getBankMenu().open(player);
            return;
        }

        if (event.getSlot() >= 45 && event.getSlot() < 54) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player player)) { return; }

        City city = CityManager.getPlayerCity(player.getUniqueId()); // Ca permets de charger les villes en background
        if (city == null) { return; }

        Inventory inv = event.getInventory();
        BankMenu menu = city.getBankMenu();
        if (menu == null) { return; }
        if (inv != menu.getInventory()) { return; }

        exit(city, inv, menu);
    }

    private void exit(City city, Inventory inv, BankMenu menu) {
        for (int i = 45; i < 54; i++) {
            inv.clear(i);
        }

        city.saveBankContent(menu.getPage(), inv.getContents());

        city.setBankMenu(null);
        city.setBankWatcher(null);
    }
}
