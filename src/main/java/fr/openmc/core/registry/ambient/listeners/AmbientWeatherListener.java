package fr.openmc.core.registry.ambient.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.PlayerWeatherNMS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class AmbientWeatherListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        for (Player player : event.getWorld().getPlayers()) {
            if (!CustomAmbient.ACTIVE_AMBIENTS.containsKey(player.getUniqueId())) continue;

            CustomAmbient ambientApplied = OMCRegistry.CUSTOM_AMBIENTS.getOrThrow(
                    CustomAmbient.ACTIVE_AMBIENTS.get(player.getUniqueId())
            );

            if (ambientApplied.getAmbientBuilder().getWeatherFixed() == null) continue;

            PlayerWeatherNMS.setWeather(player, ambientApplied.getAmbientBuilder().getWeatherFixed());
        }
    }
}