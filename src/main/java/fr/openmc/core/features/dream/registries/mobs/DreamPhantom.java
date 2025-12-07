package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;

import java.util.List;

public class DreamPhantom extends DreamMob {

    public DreamPhantom() {
        super("dream_phantom",
                "Phantom Réveillé",
                EntityType.PHANTOM,
                10.0,
                3L,
                0.4,
                RandomUtils.randomBetween(0.4, 0.8),
                List.of()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public EntitySnapshot createSnapshot(Location location) {
        World world = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (world == null) return null;
        Phantom phantom = world.createEntity(new Location(world, 0, 0, 0), Phantom.class);

        applyStats(phantom);

        phantom.setAnchorLocation(location);
        phantom.setGlowing(true);
        phantom.setLootTable(null);

        return phantom.createSnapshot();
    }
}