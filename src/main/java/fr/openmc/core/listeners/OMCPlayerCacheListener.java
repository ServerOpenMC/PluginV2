package fr.openmc.core.listeners;

import fr.openmc.api.omcplayer.OMCPlayerImpl;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CachePlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OMCPlayerCacheListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CachePlayerProfile.setPlayerProfile(player.getUniqueId(), player.getPlayerProfile());
        SkullUtils.updatePlayerHeadCache(player.getUniqueId(), player.getPlayerProfile());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        OMCPlayerImpl.removeCache(event.getPlayer().getUniqueId());
    }
}
