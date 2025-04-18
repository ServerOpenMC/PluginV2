package fr.openmc.core.listeners;

import fr.openmc.core.commands.utils.SpawnManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        // Verifier si le joueur a deja joué
        if (player.hasPlayedBefore()) return;
        player.teleport(SpawnManager.getInstance().getSpawnLocation());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (player.getRespawnLocation() != null) return;
        event.setRespawnLocation(SpawnManager.getInstance().getSpawnLocation());
    }

}
