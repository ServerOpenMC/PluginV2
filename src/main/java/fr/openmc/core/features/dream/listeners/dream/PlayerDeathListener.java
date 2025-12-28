package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isInDream(player)) return;

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        event.setCancelled(true);
        event.getDrops().clear();

        dreamPlayer.teleportToOldLocation();
    }
}
