package fr.openmc.core.features.dream.mecanism.sfx.ghost.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.mecanism.sfx.ghost.DreamGhostManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DreamGhostManager.removeGhost(player);

        for (Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(OMCPlugin.getInstance(), player);
            player.showPlayer(OMCPlugin.getInstance(), other);
        }
    }
}
