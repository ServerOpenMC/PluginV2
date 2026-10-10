package fr.openmc.core.features.settings.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerSettingsListener implements Listener {
    private final PlayerSettingsManager PLAYER_SETTINGS = OMCRegistry.FEATURES.PLAYER_SETTINGS.get();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        PLAYER_SETTINGS.loadPlayerSettings(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PLAYER_SETTINGS.unloadPlayerSettings(uuid);
    }
}
