package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/*
  Protection si le joueur se reco dans la dimension des reves.
 */
public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinInDream(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();

        if (!DreamUtils.isDreamWorld(world)) return;

        player.teleportAsync(SpawnManager.getSpawnLocation());
    }
}
