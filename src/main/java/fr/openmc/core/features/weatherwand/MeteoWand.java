package fr.openmc.core.features.weatherwand;

import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class MeteoWand extends CustomItem {

    public MeteoWand() {
        super("meteo_wand");
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Meteo Wand");
            meta.setLore(List.of("§7Changer la météo du monde !"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public ItemStack getItemsAdder() {
        return null;
    }

    /**
     * Gère l'utilisation du bâton Meteo Wand par le joueur
     */
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        openWeatherMenu(player);
    }

    /**
     * Crée et ouvre une interface utilisateur pour que le joueur change la météo
     */
    private void openWeatherMenu(Player player) {
        Bukkit.getScheduler().runTask(fr.openmc.core.OMCPlugin.getInstance(), () -> {
            WeatherGUI gui = new WeatherGUI();
            player.openInventory(gui.getInventory());
        });
    }
}