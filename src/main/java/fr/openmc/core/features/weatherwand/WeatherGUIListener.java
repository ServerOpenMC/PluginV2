package fr.openmc.core.features.weatherwand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Listener pour gérer les interactions dans l'inventaire "WeatherGUI"
 */
public class WeatherGUIListener implements Listener {

    private static final String WEATHER_GUI_TITLE = "§9Changer la météo";

    /**
     * Gère les clics dans l'inventaire "WeatherGUI"
     *
     * @param event L'événement de clic.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !WEATHER_GUI_TITLE.equals(event.getView().getTitle())) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName())
            return;

        String displayName = clickedItem.getItemMeta().getDisplayName();
        switch (displayName) {
            case "§eSoleil":
                player.getWorld().setWeatherDuration(0);
                player.getWorld().setStorm(false);
                player.sendMessage("§aVous avez changé la météo en : §eSoleil");
                break;
            case "§bPluie":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                player.sendMessage("§aVous avez changé la météo en : §bPluie");
                break;
            case "§8Orage":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                player.sendMessage("§aVous avez changé la météo en : §8Orage");
                break;
            case "§fNeige":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                player.getWorld().setWeatherDuration(6000);
                player.sendMessage("§bVous avez changé la météo en : §fNeige");
                break;
            default:
                player.sendMessage("§cOption inconnue.");
                return;
        }

        player.closeInventory();
    }
}