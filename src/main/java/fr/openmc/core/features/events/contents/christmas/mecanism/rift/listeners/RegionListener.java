package fr.openmc.core.features.events.contents.christmas.mecanism.rift.listeners;

import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import fr.openmc.core.registry.regions.events.EntityCustomRegionEnterEvent;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class RegionListener implements Listener {

    @EventHandler
    public void onRiftEnter(EntityCustomRegionEnterEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!RiftManager.getRifts().contains(UUID.fromString(event.getRegion().getKey()))) return;

        Location spawnLoc = player.getLocation().clone();
        spawnLoc.setWorld(ChristmasDimensionManager.CHRISTMAS_WORLD);

        PlayerUtils.sendFadeTitleTeleport(player,
                LocationUtils.findSafeSpawnLocation(ChristmasDimensionManager.CHRISTMAS_WORLD,
                        spawnLoc,
                        0,
                        100,
                        20).add(0, 1, 0));
    }
}
