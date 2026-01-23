package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.DreamEndEvent;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDreamTimeEndListener implements Listener {

    @EventHandler
    public void onTimeEnd(DreamEndEvent event) {
        Player player = event.getPlayer();

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        dreamPlayer.teleportToOldLocation();
    }
}
