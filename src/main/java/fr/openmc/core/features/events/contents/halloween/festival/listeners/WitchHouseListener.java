package fr.openmc.core.features.events.contents.halloween.festival.listeners;

import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.WitchHouseManager;
import fr.openmc.core.registry.regions.CustomRegion;
import fr.openmc.core.registry.regions.events.EntityCustomRegionEnterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class WitchHouseListener implements Listener {

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
                WitchHouseManager.getWitchHouseAmbient().reset(player);

            if (player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                WitchHouseManager.getWitchHouseAmbient().apply(player);
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

        if (!WitchHouseManager.enabledRegions.contains(region)) return;

        if (event.getEntity() instanceof Player player)
            WitchHouseManager.repulsePlayer(region, player, 0.3);
    }
}
