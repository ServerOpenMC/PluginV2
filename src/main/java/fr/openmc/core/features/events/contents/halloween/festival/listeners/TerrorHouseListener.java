package fr.openmc.core.features.events.contents.halloween.festival.listeners;

import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floor;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floors;
import fr.openmc.core.registry.regions.CustomRegion;
import fr.openmc.core.registry.regions.events.EntityCustomRegionEnterEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TerrorHouseListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD)) return;

        player.teleport(SpawnManager.getSpawnLocation());
        // TODO message
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (HalloweenManager.getHalloweenDB().isActive()) {
            if (event.getFrom().equals(DimensionGenerator.HALLOWEEN_WORLD))
                TerrorHouseManager.getWitchHouseAmbient().reset(player);

            if (player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                TerrorHouseManager.getWitchHouseAmbient().apply(player);
        } else {
            if (player.isOp()) return;

            if (player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD)) {
                player.teleport(SpawnManager.getSpawnLocation());
                // TODO message
            }
        }
    }

    @EventHandler
    public void onHouseRegionEnter(EntityCustomRegionEnterEvent event) {
        CustomRegion region = event.getRegion();

        if (!region.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        Entity entity = event.getEntity();

        Floors floors = TerrorHouseManager.enabledFloors.stream()
                .filter(floor -> floor.getFloor().getRegion().equals(region))
                .findAny().orElse(null);

        if (floors == null) {
            checkAndRepulse(region, entity);
            return;
        }

        Floor floor = floors.getFloor();

        if (floor == null) {
            checkAndRepulse(region, entity);
            return;
        }

        if (TerrorHouseManager.limitPassed()) return;

        if (floor.canSpawn) {
            floor.spawnMob();
        }
    }

    private void checkAndRepulse(CustomRegion region, Entity entity) {
        if (TerrorHouseManager.useRegions.contains(region))
            if (entity instanceof Player player) {
                TerrorHouseManager.repulsePlayer(region, player, 0.3);
                // TODO message
            }
    }
}
