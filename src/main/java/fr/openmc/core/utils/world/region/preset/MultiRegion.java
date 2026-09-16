package fr.openmc.core.utils.world.region.preset;

import fr.openmc.core.utils.world.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiRegion extends Region {

    private final List<Region> regions;

    private final World regionWorld;

    public MultiRegion(World regionWorld, Region... regions) {
        this.regionWorld = regionWorld;
        this.regions = new ArrayList<>(List.of(regions));
        super.initChunks();
    }

    // DEBUG //
//    public void addDetection() {
//        super.addDetection(
//                player -> true,
//                player -> player.sendMessage("test tu es entré dans la zone")
//        );
//    }

    public void addDetection(Consumer<Entity> action) {
        super.addDetection(entity -> true, action);
    }

    @Override
    public boolean isInside(Location loc) {
        for (Region region : regions)
            if (region.isInside(loc)) return true;
        return false;
    }

    @Override
    public World getWorld() {
        return regionWorld;
    }

    @Override
    public Set<Chunk> getChunks() {
        Set<Chunk> chunks = new HashSet<>();

        for (Region region : regions) {

            if (region.getWorld() != regionWorld) {
                continue;
            }

            chunks = Stream.concat(chunks.stream(), region.getChunks().stream())
                    .collect(Collectors.toCollection(HashSet::new));
        }

        return chunks;
    }
}
