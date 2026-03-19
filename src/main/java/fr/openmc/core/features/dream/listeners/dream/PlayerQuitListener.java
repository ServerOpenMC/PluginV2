package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.sfx.PlayerCloneNpc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitWhenDream(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isInDream(player)) return;

        if (PlayerCloneNpc.getCloneNpc(player) != null)
            PlayerCloneNpc.deleteCloneNpc(player);
        DreamManager.removeDreamPlayer(player, player.getLocation());
    }
}
