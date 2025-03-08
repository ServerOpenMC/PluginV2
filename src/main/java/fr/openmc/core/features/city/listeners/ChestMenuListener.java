package fr.openmc.core.features.city.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.ChestMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestMenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) { return; }

        City city = CityManager.getPlayerCity(player.getUniqueId()); // Ca permets de charger les villes en background
        if (city == null) { return; }

        Inventory inv = event.getInventory();
        ChestMenu menu = city.getChestMenu();
        if (menu == null) { return; }
        if (inv != menu.getInventory()) { return; }

        // L'inventaire est la banque de ville, on peut *enfin* faire qqchose

        if (event.getSlot() == 48 && event.getCurrentItem().getType() == Material.ENDER_CHEST) { // Upgrade Button
            int price = city.getChestPages()*5000; // fonction linéaire f(x)=ax ; a=5000
            if (city.getBalance() < price) {
                MessagesManager.sendMessage(player, Component.text("La ville n'as pas assez d'argent ("+price+" nécessaires)"), Prefix.CITY, MessageType.ERROR, true);
                player.closeInventory();
                return;
            }

            city.updateBalance((double) -price);

            city.upgradeChest();
            MessagesManager.sendMessage(player, Component.text("Le coffre a été amélioré"), Prefix.CITY, MessageType.SUCCESS, true);
            player.closeInventory();
            return;
        }

        if (event.getSlot() == 49) { // Close Button
            exit(city, inv, menu);
            player.closeInventory();
            return;
        }

        if (event.getSlot() == 45 && menu.hasPreviousPage()) { // Previous Button
            city.setChestMenu(new ChestMenu(city, menu.getPage() - 1));
            city.getChestMenu().open(player);
            return;
        }

        if (event.getSlot() == 53 && menu.hasNextPage()) { // Next Button
            city.setChestMenu(new ChestMenu(city, menu.getPage() + 1));
            city.getChestMenu().open(player);
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
        ChestMenu menu = city.getChestMenu();
        if (menu == null) { return; }
        if (inv != menu.getInventory()) { return; }

        exit(city, inv, menu);
    }

    private void exit(City city, Inventory inv, ChestMenu menu) {
        for (int i = 45; i < 54; i++) {
            inv.clear(i);
        }

        city.saveChestContent(menu.getPage(), inv.getContents());

        city.setChestMenu(null);
        city.setChestWatcher(null);
    }
}
