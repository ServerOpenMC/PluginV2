package fr.openmc.core.features.dream.generation.populators.glacite;

import fr.openmc.core.utils.structure.FeaturesPopulator;
import fr.openmc.core.utils.structure.StructureUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MIN_HEIGHT_MUD;


public class GlaciteGeodePopulator extends FeaturesPopulator {
    private static final double CHUNK_GEODE_PROBABILITY = 0.1;

    public GlaciteGeodePopulator() {
        super("omc_dream", List.of("glacite/geode"));
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= CHUNK_GEODE_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = MIN_CAVE_HEIGHT + random.nextInt(MIN_HEIGHT_MUD - MIN_CAVE_HEIGHT);

        Location loc = new Location(world, x, y, z);

        StructureUtils.CachedStructure structure = getRandomFeatures(random);
        placeFeatures(structure, loc, random.nextBoolean(), random.nextBoolean(), true);
    }
}
