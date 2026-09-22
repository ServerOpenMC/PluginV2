package fr.openmc.core.features.singularity.sub.world.singularity;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.cube.listeners.RepulseEffectListener;
import fr.openmc.core.features.singularity.sub.world.SingularityWorldManager;
import fr.openmc.core.registry.regions.CustomRegion;
import fr.openmc.core.utils.bukkit.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Set;

public class SingularityInteractionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Set<CustomRegion> regions = OMCRegistry.CUSTOM_REGIONS.getByLocation(clickedBlock.getLocation());
        if (regions.isEmpty()) return;
        if (!regions.contains(OMCRegistry.CUSTOM_REGIONS.SINGULARITY_REGION)) return;

        repulseBySingularity(event.getPlayer());
    }

    private void repulseBySingularity(Player player) {
        Location origin = SingularityWorldManager.getOrigin();

        Vector velocity = player.getLocation().toVector()
                .subtract(origin.toVector());
        velocity.setY(0);

        if (velocity.lengthSquared() == 0) return;

        velocity.normalize().multiply(5);

        EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, 0.001);

        player.setVelocity(velocity);

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_BEACON_POWER_SELECT,
                3.0f,
                1.6f
        );

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, 0f), 5L);
    }
}
