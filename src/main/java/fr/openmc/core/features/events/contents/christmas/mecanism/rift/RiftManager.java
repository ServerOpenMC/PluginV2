package fr.openmc.core.features.events.contents.christmas.mecanism.rift;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.commands.AdminRiftCommands;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.listeners.RegionListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.listeners.StructureListener;
import fr.openmc.core.registry.regions.preset.SquareRegion;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RiftManager extends Feature implements HasListeners, HasCommands {
    private static final Set<UUID> rifts = new HashSet<>();

    public static void placeRift(Location loc) {
        UUID riftUUID = UUID.randomUUID();
        try {
            rifts.add(riftUUID);
            OMCRegistry.CUSTOM_STRUCTURES.CHRISTMAS_RIFT.place(riftUUID, loc);
        } catch (Exception exception) {
            rifts.remove(riftUUID);
            OMCLogger.error("Erreur lors de la création de la faille de noel", exception);
        }
    }

    public static void removeRift(UUID riftUUID) {
        try {
            OMCRegistry.CUSTOM_STRUCTURES.CHRISTMAS_RIFT.remove(riftUUID);
            rifts.remove(riftUUID);
        } catch (Exception exception) {
            OMCLogger.error("Erreur lors de la création de la faille de noel", exception);
        }
    }

    public static void registerRegion(UUID riftUUID, World world, BlockVector3 min, BlockVector3 max) {
        OMCRegistry.CUSTOM_REGIONS.register(new SquareRegion(riftUUID.toString(),
                world.getName(),
                min,
                max));
    }

    public static void unregisterRegion(UUID riftUUID) {
        OMCRegistry.CUSTOM_REGIONS.unregister(riftUUID.toString());
    }

    public static Set<UUID> getRifts() {
        return new HashSet<>(rifts);
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                StructureListener::new,
                RegionListener::new
        );
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminRiftCommands()
        );
    }
}
