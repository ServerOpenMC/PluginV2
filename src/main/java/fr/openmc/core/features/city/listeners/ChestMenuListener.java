package fr.openmc.core.features.city.listeners;

import org.bukkit.event.Listener;

public class ChestMenuListener implements Listener {
//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        if (Restart.isRestarting) return;
//
//        HumanEntity humanEntity = event.getWhoClicked();
//        if (!(humanEntity instanceof Player player)) { return; }
//
//        City city = CityManager.getPlayerCity(player.getUniqueId()); // Permet de charger les villes en background
//        if (city == null) { return; }
//
//        Inventory inv = event.getInventory();
//        ChestMenu menu = city.getChestMenu();
//        if (menu == null) { return; }
//        if (inv != menu.getInventory()) { return; }
//
//        // L'inventaire est la banque de ville, on peut *enfin* faire quelque chose
//
//        if (event.getSlot() == 45 && menu.hasPreviousPage()) {
//            city.saveChestContent(menu.getPage(), inv.getContents());// Previous Button
//            city.setChestMenu(new ChestMenu(city, menu.getPage() - 1));
//            city.getChestMenu().open(player);
//            return;
//        }
//
//        if (event.getSlot() == 53 && menu.hasNextPage()) {
//            city.saveChestContent(menu.getPage(), inv.getContents());// Next Button
//            city.setChestMenu(new ChestMenu(city, menu.getPage() + 1));
//            city.getChestMenu().open(player);
//            return;
//        }
//
//        if (event.getSlot() >= 45 && event.getSlot() < 54) {
//            event.setCancelled(true);
//        }
//    }
}
