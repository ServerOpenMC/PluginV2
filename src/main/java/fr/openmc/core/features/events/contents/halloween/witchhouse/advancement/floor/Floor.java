package fr.openmc.core.features.events.contents.halloween.witchhouse.advancement.floor;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.halloween.witchhouse.TerrorHouseDimensionManager;
import fr.openmc.core.features.events.contents.halloween.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.registry.regions.preset.SquareRegion;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.world.LocationUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Floor {

    private final SquareRegion region;
    private final List<CustomMobEntry> entities;
    private final List<BlockVector3> spawnLocations;
    private final int entityToSpawn;

    private final int RADIUS = 3;

    private final List<Entity> clearLag = new ArrayList<>();
    private BukkitTask spawnTask;
    private int timeTask = 0;

    public Floor(SquareRegion region, List<CustomMobEntry> entities, int entityToSpawn, List<BlockVector3> spawnLocations) {
        this.region = region;
        this.entities = entities;
        this.entityToSpawn = entityToSpawn;
        this.spawnLocations = spawnLocations;
    }

    public void enableSpawn() {

        spawnTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {

            timeTask += 30;

            if (timeTask>=300) // 5 minutes
                clearLag();

            if (entities.isEmpty()) {
                OMCLogger.warn("No mob register for the spawn mob, spawn disable for this floor");
                clearLag();
                spawnTask.cancel();
                return;
            }

            if (spawnLocations.isEmpty()) {
                OMCLogger.warn("No spawn location register for the spawn mob, spawn disable for this floor");
                clearLag();
                spawnTask.cancel();
                return;
            }

            CustomMobEntry mobToSpawn = RandomUtils.getRandomObjectFromList(entities);

            if (mobToSpawn == null)
                return;

            if (!hasPlayer())
                return;

            if (TerrorHouseManager.limitPassed())
                return;

            int toSpawn = RandomUtils.randomBetween(1, entityToSpawn);

            for (int i = 0; i < toSpawn; i++)
                clearLag.add(mobToSpawn.spawn(getRandomLocation()));

            TerrorHouseManager.entityCount += toSpawn;
        }, 0L, 15 * 20L);
    }

    private void clearLag() {
        for (Entity entity : clearLag)
            entity.remove();
        clearLag.clear();
    }

    public Location getRandomLocation() {

        BlockVector3 pos = RandomUtils.getRandomObjectFromList(spawnLocations);

        Location newloc = LocationUtils.randomLocation(
                LocationUtils.fromVector(
                        TerrorHouseDimensionManager.HALLOWEEN_WORLD,
                        pos
                ),
                RADIUS
        );

        newloc.setY(pos.y());

        return newloc;
    }

    public boolean hasPlayer() {
        return TerrorHouseDimensionManager.HALLOWEEN_WORLD.getPlayers()
                .stream()
                .filter(p -> region.contains(p.getLocation()))
                .findAny().orElse(null) != null;
    }
}
