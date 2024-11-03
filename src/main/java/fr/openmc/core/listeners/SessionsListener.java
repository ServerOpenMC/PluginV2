package fr.openmc.core.listeners;

import fr.openmc.core.features.analytics.Stats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SessionsListener implements Listener {
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Stats.SESSION.increment(event.getPlayer().getUniqueId());
    }
}
