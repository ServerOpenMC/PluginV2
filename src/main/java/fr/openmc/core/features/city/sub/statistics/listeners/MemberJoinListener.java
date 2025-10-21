package fr.openmc.core.features.city.sub.statistics.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDate;
import java.util.UUID;

public class MemberJoinListener implements Listener {
    
    // todo: implement city inactivity for cleaning innactive city (check https://github.com/ServerOpenMC/PluginV2/issues/247)

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID playerUUID = e.getPlayer().getUniqueId();

        City playerCity = CityManager.getPlayerCity(playerUUID);

        if (playerCity == null) return;

        CityStatisticsManager.setStat(playerCity.getUniqueId(), "last_activity", LocalDate.now());
    }
}
