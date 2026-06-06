package fr.openmc.core.registry.ambient.listeners;

import fr.openmc.core.registry.ambient.CustomAmbient;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CustomAmbientListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CustomAmbient.ACTIVE_AMBIENTS.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        CustomAmbient.ACTIVE_AMBIENTS.remove(event.getPlayer().getUniqueId());
    }
}

