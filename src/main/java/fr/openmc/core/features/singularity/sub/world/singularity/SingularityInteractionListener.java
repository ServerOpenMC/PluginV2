package fr.openmc.core.features.singularity.sub.world.singularity;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.cube.listeners.RepulseEffectListener;
import fr.openmc.core.features.singularity.sub.world.SingularityWorldManager;
import fr.openmc.core.registry.poi.CustomPoi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Optional;

public class SingularityInteractionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Optional<CustomPoi> poi = OMCRegistry.CUSTOM_POI.getByLocation(clickedBlock.getLocation());
        if (poi.isEmpty()) return;
        if (!poi.get().equals(OMCRegistry.CUSTOM_POI.SINGULARITY)) return;

        repulseBySingularity(event.getPlayer());
    }

    private void repulseBySingularity(Player player) {
        Location originSingularity = SingularityWorldManager.getOrigin();
        Vector velocity = player.getLocation().toVector().subtract(originSingularity.toVector()).normalize();

        velocity.setY(player.getLocation().getY() - originSingularity.getY());

        velocity.multiply(3);

        player.setVelocity(velocity);

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 3.0f, 1.6f);
    }
}
