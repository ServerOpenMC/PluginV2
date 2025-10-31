package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isInDreamWorld(player)) return;

        event.getDrops().clear();
        DreamManager.removeDreamPlayer(player, player.getLocation());
    }
}
