package fr.openmc.core.registry.regions;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.registry.regions.content.ExampleRegion;
import fr.openmc.core.registry.regions.preset.SquareRegion;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomRegionRegistry extends Registry<Key, CustomRegion>
        implements KeyedRegistry<Key, CustomRegion> {

    public final ExampleRegion EXAMPLE_REGION = register(new ExampleRegion());

    //Terror House Floor

    private final int MIN_X = -91;
    private final int MAX_X = -22;
    private final int MIN_Z = -94;
    private final int MAX_Z = -28;

    public final SquareRegion FLOOR_1 = register(new SquareRegion(
            "floor_1",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -60, MIN_Z),
            BlockVector3.at(MAX_X, -53, MAX_Z)
    ));

    public final SquareRegion FLOOR_2 = register(new SquareRegion(
            "floor_2",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -52, MIN_Z),
            BlockVector3.at(MAX_X, -44, MAX_Z)
    ));

    public final SquareRegion FLOOR_3 = register(new SquareRegion(
            "floor_3",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -44, MIN_Z),
            BlockVector3.at(MAX_X, -36, MAX_Z)
    ));

    public final SquareRegion FLOOR_4 = register(new SquareRegion(
            "floor_4",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -36, MIN_Z),
            BlockVector3.at(MAX_X, -28, MAX_Z)
    ));

    public final SquareRegion FLOOR_5 = register(new SquareRegion(
            "floor_5",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -28, MIN_Z),
            BlockVector3.at(MAX_X, -20, MAX_Z)
    ));

    public final SquareRegion FLOOR_6 = register(new SquareRegion(
            "floor_6",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -20, MIN_Z),
            BlockVector3.at(MAX_X, -12, MAX_Z)
    ));

    public final SquareRegion FLOOR_7 = register(new SquareRegion(
            "floor_7",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -12, MIN_Z),
            BlockVector3.at(MAX_X, -4, MAX_Z)
    ));

    public final SquareRegion FLOOR_8 = register(new SquareRegion(
            "floor_8",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, -4, MIN_Z),
            BlockVector3.at(MAX_X, 4, MAX_Z)
    ));

    public final SquareRegion FLOOR_9 = register(new SquareRegion(
            "floor_9",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 4, MIN_Z),
            BlockVector3.at(MAX_X, 12, MAX_Z)
    ));

    public final SquareRegion FLOOR_10 = register(new SquareRegion(
            "floor_10",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 12, MIN_Z),
            BlockVector3.at(MAX_X, 20, MAX_Z)
    ));

    public final SquareRegion FLOOR_11 = register(new SquareRegion(
            "floor_11",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 20, MIN_Z),
            BlockVector3.at(MAX_X, 27, MAX_Z)
    ));
    public final SquareRegion FLOOR_12 = register(new SquareRegion(
            "floor_12",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 28, MIN_Z),
            BlockVector3.at(MAX_X, 36, MAX_Z)
    ));
    public final SquareRegion FLOOR_13 = register(new SquareRegion(
            "floor_13",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 36, MIN_Z),
            BlockVector3.at(MAX_X, 44, MAX_Z)
    ));
    public final SquareRegion FLOOR_14 = register(new SquareRegion(
            "floor_14",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 45, MIN_Z),
            BlockVector3.at(MAX_X, 55, MAX_Z)
    ));

    public final SquareRegion FLOOR_15 = register(new SquareRegion(
            "floor_15",
            DimensionGenerator.DIMENSION_NAME,
            BlockVector3.at(MIN_X, 53, MIN_Z),
            BlockVector3.at(MAX_X, 68, MAX_Z)
    ));

    @Override
    public Key key(CustomRegion registryObject) {
        return registryObject.getKey();
    }

    public Set<CustomRegion> getByLocation(Location location) {
        if (location == null || location.getWorld() == null) return Collections.emptySet();

        Set<CustomRegion> regions = new HashSet<>();

        for (CustomRegion region : values()) {
            if (region.contains(location)) {
                regions.add(region);
            }
        }

        return regions;
    }
}