package fr.openmc.core.utils.structure;

import org.bukkit.Location;
import org.bukkit.generator.BlockPopulator;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class FeaturesPopulator extends BlockPopulator {

    public final String group;
    public final List<String> features;

    public FeaturesPopulator(String group, List<String> features) {
        this.group = group;
        this.features = features;

        Map<String, List<String>> toPreload = Map.of(group, features);
        StructureUtils.preloadStructures(toPreload);
    }

    protected StructureUtils.CachedStructure getRandomFeatures(Random random) {
        if (features.isEmpty()) return null;
        String name = features.get(random.nextInt(features.size()));
        return StructureUtils.getCachedStructure(group, name);
    }

    protected void placeFeatures(StructureUtils.CachedStructure structure, Location target, boolean mirrorX, boolean mirrorZ, boolean placeAir) {
        if (structure == null) return;
        StructureUtils.placeStructure(structure, target, mirrorX, mirrorZ, placeAir);
    }
}
