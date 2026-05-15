package fr.openmc.core.features.settings.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static fr.openmc.core.features.settings.PlayerSettingsManager.loadPlayerSettings;
import static fr.openmc.core.features.settings.PlayerSettingsManager.unloadPlayerSettings;

public class PlayerSettingsListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerSettings(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        unloadPlayerSettings(uuid);
    }
}
