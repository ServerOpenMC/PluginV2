package fr.openmc.core.listeners;

import fr.openmc.core.commands.utils.SpawnManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (player.getRespawnLocation() != null) return;
        event.setRespawnLocation(SpawnManager.getInstance().getSpawnLocation());
    }

}
