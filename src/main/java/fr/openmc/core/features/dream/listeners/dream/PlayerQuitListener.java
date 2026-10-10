package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.sfx.clone.PlayerCloneNpc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final DreamManager dreamManager;
    private final PlayerCloneNpc playerCloneNpc;

    public PlayerQuitListener(DreamManager manager) {
        this.dreamManager = manager;
        this.playerCloneNpc = OMCRegistry.DREAM_FEATURES.PLAYER_CLONE_NPC;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitWhenDream(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isInDream(player)) return;

        if (playerCloneNpc.getCloneNpc(player) != null)
            playerCloneNpc.deleteCloneNpc(player);
        dreamManager.removeDreamPlayer(player, player.getLocation());
    }
}
