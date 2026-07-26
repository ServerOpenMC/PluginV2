package fr.openmc.core.listeners;

import fr.openmc.api.entity.player.OMCPlayerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OMCPlayerCacheListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        OMCPlayerImpl.removeCache(event.getPlayer().getUniqueId());
    }
}
