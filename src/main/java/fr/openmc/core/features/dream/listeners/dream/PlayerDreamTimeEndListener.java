package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.DreamEndEvent;
import fr.openmc.core.features.dream.mecanism.sfx.clone.PlayerCloneNpc;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDreamTimeEndListener implements Listener {
    private final DreamManager dreamManager;
    private final PlayerCloneNpc playerCloneNpc;

    public PlayerDreamTimeEndListener(DreamManager manager) {
        this.dreamManager = manager;
        this.playerCloneNpc = OMCRegistry.DREAM_FEATURES.PLAYER_CLONE_NPC;
    }

    @EventHandler
    public void onTimeEnd(DreamEndEvent event) {
        Player player = event.getPlayer();

        playerCloneNpc.deleteCloneNpc(player);
        DreamPlayer dreamPlayer = dreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        dreamPlayer.teleportToOldLocation();
    }
}
