package fr.openmc.core.features.dream.generation.effects;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BiomeParticleListener implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) {
            BiomeParticleManager.startTask(player);
        } else {
            BiomeParticleManager.stopTask(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BiomeParticleManager.stopTask(event.getPlayer());
    }
}
