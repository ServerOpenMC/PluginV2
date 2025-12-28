package fr.openmc.core.features.dream.generation.biomes;

import fr.openmc.core.utils.FastNoiseLite;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CloudChunkGenerator {
    public static final int MIN_HEIGHT_CLOUD = 120;
    public static final int MAX_HEIGHT_CLOUD = 124;

    public static final FastNoiseLite cloudNoise = new FastNoiseLite();

    public static void init(long seed) {
        cloudNoise.SetSeed((int) seed);
        cloudNoise.SetFrequency(0.06f);
    }

    public static void generateBlock(@NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.ChunkData chunkData, int x, int y, int z) {
        if (y >= MIN_HEIGHT_CLOUD && y <= MAX_HEIGHT_CLOUD) {
            // noise principal pour la densité des nuages
            float cloudNoiseValue = cloudNoise.GetNoise(
                    (x + (chunkX * 16)) * 0.8f,
                    (z + (chunkZ * 16)) * 0.8f
            );

            float normalized = (cloudNoiseValue + 1) / 2f;

            if (normalized > 0.5f) {
                float distToCenter = Math.abs(y - 112.5f);
                float verticalFactor = Math.max(0.2f, 1.5f - (distToCenter / 2f));
                float baseDensity = 0.65f;
                float finalDensity = Math.max(baseDensity, normalized * verticalFactor);

                if (random.nextFloat() < finalDensity) {
                    chunkData.setBlock(x, y, z, Material.POWDER_SNOW);
                }
            }
        }
    }
}
