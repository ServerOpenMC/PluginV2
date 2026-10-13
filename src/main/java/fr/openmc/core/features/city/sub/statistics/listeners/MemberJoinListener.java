package fr.openmc.core.features.city.sub.statistics.listeners;

import fr.openmc.core.features.city.models.city.City;
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

        City playerCity = City.ofPlayer(playerUUID);

        if (playerCity == null) return;

        playerCity.setStat("last_activity", LocalDate.now());
    }
}
