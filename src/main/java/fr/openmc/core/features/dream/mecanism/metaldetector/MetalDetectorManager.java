package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.loottable.CustomLootTable;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class MetalDetectorManager extends Feature implements HasListeners {
    public final Map<UUID, MetalDetectorTask> hiddenChests = new HashMap<>();

    public final CustomLootTable METAL_DETECTOR_LOOT_TABLE = OMCRegistry.CUSTOM_LOOT_TABLES.METAL_DETECTOR;

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                () -> new MetalDetectorListener(this)
        );
    }

    public Location findRandomChestLocation(Location origin) {
        World world = origin.getWorld();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            int dx = random.nextInt(41) - 20;
            int dz = random.nextInt(41) - 20;
            Location tryLoc = origin.clone().add(dx, 0, dz);
            int y = world.getHighestBlockYAt(tryLoc);
            tryLoc.setY(y);

            if (DreamBiome.isDreamBiome(tryLoc, DreamBiome.MUD_BEACH)) {
                return tryLoc;
            }
        }

        return origin.clone().add(random.nextInt(41) - 20, 0, random.nextInt(41) - 20);
    }
}
