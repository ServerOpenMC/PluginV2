package fr.openmc.core.registry.regions;

import com.sk89q.worldedit.math.BlockVector3;
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

    public final SquareRegion TEST_REGION = register(new SquareRegion(
            DimensionGenerator.HALLOWEEN_WORLD,
            BlockVector3.at(0, 0, 0),
            BlockVector3.at(1, 1, 1)
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