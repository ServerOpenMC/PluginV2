package fr.openmc.core.features.weatherwand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class WeatherGUI {
    private final Inventory inventory;

    private final HashMap<UUID, Long> playerCooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 30 * 1000;

    public WeatherGUI() {
        this.inventory = Bukkit.createInventory(null, 9, "§9Changer la météo");

        addWeatherItem(0, Material.SUNFLOWER, "§eSoleil");
        addWeatherItem(2, Material.WATER_BUCKET, "§bPluie");
        addWeatherItem(4, Material.CREEPER_HEAD, "§8Orage");
        addWeatherItem(6, Material.SNOWBALL, "§fNeige");
    }

    private void addWeatherItem(int slot, Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gère la sélection d'une météo par l'utilisateur avec gestion de cooldown.
     */
    public void handleInventoryClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (playerCooldowns.containsKey(playerId)) {
            long lastUseTime = playerCooldowns.get(playerId);
            if ((currentTime - lastUseTime) < COOLDOWN_TIME) {
                long timeLeft = (COOLDOWN_TIME - (currentTime - lastUseTime)) / 1000;
                player.sendMessage("§cVous devez attendre encore " + timeLeft + " secondes avant de changer la météo !");
                return;
            }
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();

        switch (displayName) {
            case "§eSoleil":
                player.getWorld().setWeatherDuration(0);
                player.getWorld().setStorm(false);
                break;
            case "§bPluie":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                break;
            case "§8Orage":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                break;
            case "§fNeige":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                player.getWorld().setWeatherDuration(6000);
                player.sendMessage("§bVous avez invoqué une tempête de neige !");
                break;
            default:
                return;
        }

        player.closeInventory();
        player.sendMessage("§aVous avez changé la météo en : " + displayName);
        playerCooldowns.put(playerId, currentTime);
    }
}