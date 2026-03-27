package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

/*
  Protection si le joueur se reco dans la dimension des reves.
 */
public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinInDream(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isInDreamWorld(player)) return;

        try {
            DreamManager.preloadSavePlayer(player, player.getLocation());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
