package fr.openmc.core.features.dream.mecanism.sfx.ghost.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.events.DreamEndEvent;
import fr.openmc.core.features.dream.events.DreamEnterEvent;
import fr.openmc.core.features.dream.mecanism.sfx.ghost.DreamGhostManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DreamPlayerEnteredListener implements Listener {

    @EventHandler
    public void onDreamEnter(DreamEnterEvent event) {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                DreamGhostManager.setupGhost(event.getPlayer()), 20L);
    }

    @EventHandler
    public void onDreamExit(DreamEndEvent event) {
        Player player = event.getPlayer();
        DreamGhostManager.removeGhost(player);

        for (Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(OMCPlugin.getInstance(), player);
            player.showPlayer(OMCPlugin.getInstance(), other);
        }
    }
}
