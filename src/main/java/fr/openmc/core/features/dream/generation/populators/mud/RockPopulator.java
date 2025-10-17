package fr.openmc.core.features.dream.generation.populators.mud;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.structure.FeaturesPopulator;
import fr.openmc.core.utils.structure.StructureUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;


public class RockPopulator extends FeaturesPopulator {
    private static final double ROCK_PROBABILITY = 0.6;

    public RockPopulator() {
        super("omc_dream", List.of(
                "mud/rock_1",
                "mud/rock_2",
                "mud/rock_3",
                "mud/rock_4",
                "mud/rock_5",
                "mud/rock_6",
                "mud/rock_7"
        ));
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= ROCK_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.MUD_BEACH.getBiome())) return;

        StructureUtils.CachedStructure structure = getRandomFeatures(random);
        placeFeatures(structure, loc, random.nextBoolean(), random.nextBoolean(), false);
    }
}