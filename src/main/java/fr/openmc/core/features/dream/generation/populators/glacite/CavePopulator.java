package fr.openmc.core.features.dream.generation.populators.glacite;

import fr.openmc.core.utils.structure.FeaturesPopulator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MIN_HEIGHT_MUD;

public class CavePopulator extends FeaturesPopulator {

    private final double chunkProbability;
    private final double perSolProbability;

    public CavePopulator(double chunkProbability, double perSolProbability, List<String> features) {
        super("omc_dream", features);
        this.chunkProbability = chunkProbability;
        this.perSolProbability = perSolProbability;
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= chunkProbability) return;

        int startX = chunk.getX() << 4;
        int startZ = chunk.getZ() << 4;
        int attempts = 32;

        for (int i = 0; i < attempts; i++) {
            int x = startX + random.nextInt(16);
            int z = startZ + random.nextInt(16);

            for (int y = MIN_HEIGHT_MUD - 1; y > MIN_CAVE_HEIGHT; y--) {
                Block block = world.getBlockAt(x, y, z);

                if (!block.getType().isAir() || !block.getRelative(BlockFace.DOWN).getType().equals(Material.ICE)) {
                    Block above = block.getRelative(BlockFace.UP);

                    if (above.isEmpty() || above.getType() == Material.SNOW) {
                        if (random.nextDouble() < perSolProbability) {
                            Location loc = new Location(world, x, y + 1, z);
                            placeFeatures(getRandomFeatures(random), loc, random.nextBoolean(), random.nextBoolean(), false);
                        }
                    }
                }
            }
        }
    }
}
