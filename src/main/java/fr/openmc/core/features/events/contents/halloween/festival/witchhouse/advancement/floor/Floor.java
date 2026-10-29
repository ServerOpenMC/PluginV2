package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.registry.regions.preset.SquareRegion;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.world.LocationUtils;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

@Getter
public class Floor {

    private final SquareRegion region;
    private final List<CustomMobEntry> entities;
    private final List<BlockVector3> spawnLocations;
    private final int entityToSpawn;

    private final int RADIUS = 5;

    private BukkitTask spawnTask;

    public Floor(SquareRegion region, List<CustomMobEntry> entities, int entityToSpawn, List<BlockVector3> spawnLocations) {
        this.region = region;
        this.entities = entities;
        this.entityToSpawn = entityToSpawn;
        this.spawnLocations = spawnLocations;
    }

    //TODO marche pas a fix

    public void enableSpawn() {
        spawnTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {

            if (entities.isEmpty()) {
                OMCLogger.warn("No mob register for the spawn mob, spawn disable for this floor");
                spawnTask.cancel();
                return;
            }

            if (spawnLocations.isEmpty()) {
                OMCLogger.warn("No spawn location register for the spawn mob, spawn disable for this floor");
                spawnTask.cancel();
                return;
            }

            CustomMobEntry mobToSpawn = RandomUtils.getRandomObjectFromList(entities);

            if (!hasPlayer())
                return;

            if (TerrorHouseManager.limitPassed())
                return;

            int toSpawn = RandomUtils.randomBetween(1, entityToSpawn);

            for (int i = 0; i < toSpawn; i++) {
                mobToSpawn.spawn(getRandomSafeLocationInRegion());
                OMCLogger.info("debug : spawn mob " + i);
            }

            TerrorHouseManager.entityCount += toSpawn;
        }, 0L, 5L * 20L);
    }

    public Location getRandomSafeLocationInRegion() {

        return LocationUtils.randomLocation(
                LocationUtils.fromVector(
                        DimensionGenerator.HALLOWEEN_WORLD,
                        RandomUtils.getRandomObjectFromList(spawnLocations)
                ),
                RADIUS
        );
    }

    public boolean hasPlayer() {
        return DimensionGenerator.HALLOWEEN_WORLD.getPlayers()
                .stream()
                .filter(p -> region.contains(p.getLocation()))
                .findAny().orElse(null) != null;
    }
}
