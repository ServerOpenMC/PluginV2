package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Phantom;

@SuppressWarnings("UnstableApiUsage")
public class DreamPhantom extends DreamMob<Phantom> {
    public DreamPhantom(String id) {
        super(id,
                "Phantom Réveillé",
                Phantom.class,
                10.0,
                3L,
                0.4,
                RandomUtils.randomBetween(0.4, 0.8)
        );
    }

    @Override
    public EntitySnapshot getMobSnapshot(Object... args) {
        World world = DreamDimensionManager.DREAM_WORLD;
        if (world == null) return null;
        Phantom phantom = world.createEntity(new Location(world, 0, 0, 0), Phantom.class);

        applyStats(phantom);

        phantom.setAnchorLocation((Location) args[0]);
        phantom.setGlowing(true);
        phantom.setLootTable(null);

        return phantom.createSnapshot();
    }
}