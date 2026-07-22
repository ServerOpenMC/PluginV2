package fr.openmc.core.registry.ambient.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.events.RegionEnterEvent;
import fr.openmc.core.events.RegionLeaveEvent;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.PlayerWeatherNMS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class AmbientWeatherListener implements Listener {

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        reapplyWeather(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        reapplyWeather(player);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            for (Player player : event.getWorld().getPlayers()) {
                reapplyWeather(player);
            }
        }, 1L);
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        Player player = event.getPlayer();
        System.out.println("apply");
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                reapplyWeather(player), 1L);
    }

    @EventHandler
    public void onRegionExit(RegionLeaveEvent event) {
        Player player = event.getPlayer();
        System.out.println("apply2");
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                    reapplyWeather(player), 1L);
    }

    /**
     * Réapplique la pluie/orage/temps sur un joueur
     * @param player le joueur ciblé
     */
    private void reapplyWeather(Player player) {
        if (!CustomAmbient.ACTIVE_AMBIENTS.containsKey(player.getUniqueId())) return;

        CustomAmbient ambientApplied = OMCRegistry.CUSTOM_AMBIENTS.getOrThrow(
                CustomAmbient.ACTIVE_AMBIENTS.get(player.getUniqueId()));

        if (ambientApplied.getAmbientBuilder().getWeatherFixed() == null) return;

        PlayerWeatherNMS.setWeather(player, ambientApplied.getAmbientBuilder().getWeatherFixed());
    }
}