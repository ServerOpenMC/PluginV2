package fr.openmc.core.features.city.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CityDoorsListener implements Listener {
    private boolean isRegionCity(ProtectedRegion region) {
        return region.getId().startsWith("city_");
    }

    private void sendCityActionBar(Player player, String cityName, boolean entering) {
        String actionText = entering ? "Tu entres dans " : "Tu sors de ";
        player.sendActionBar(
                Component.text(actionText).append(
                        Component.text(cityName)
                                .color(entering ? NamedTextColor.GREEN : NamedTextColor.RED)
                                .decorate(TextDecoration.BOLD)
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onPlayerEnter(RegionEnteredEvent event) {
        ProtectedRegion region = event.getRegion();
        if (!isRegionCity(region)) return;

        Player player = event.getPlayer();
        if (player == null) return;

        City city = CityManager.getCity(region.getId().substring(5));
        String cityName;
        if (city == null) {
            cityName = "une ville inconnue";
        } else {
            cityName = city.getName();
        }

        sendCityActionBar(player, cityName, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onPlayerExit(RegionLeftEvent event) {
        ProtectedRegion region = event.getRegion();
        if (!isRegionCity(region)) return;

        Player player = event.getPlayer();
        if (player == null) return;

        City city = CityManager.getCity(region.getId().substring(5));
        String cityName;
        if (city == null) {
            cityName = "une ville inconnue";
        } else {
            cityName = city.getName();
        }

        sendCityActionBar(player, cityName, false);
    }
}
