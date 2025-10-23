package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.DreamTimeEndEvent;
import fr.openmc.core.features.dream.models.DreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDreamTimeEndListener implements Listener {

    @EventHandler
    public void onTimeEnd(DreamTimeEndEvent event) {
        Player player = event.getPlayer();

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        dreamPlayer.teleportToOldLocation();
    }
}
