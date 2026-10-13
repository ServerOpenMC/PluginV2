package fr.openmc.core.registry.regions;

import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.Registry;
import fr.openmc.core.registry.regions.content.ExampleRegion;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomRegionRegistry extends Registry<Key, CustomRegion>
        implements KeyedRegistry<Key, CustomRegion> {

    public final ExampleRegion EXAMPLE_REGION = register(new ExampleRegion());

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