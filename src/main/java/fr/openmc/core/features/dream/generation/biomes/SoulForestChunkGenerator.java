package fr.openmc.core.features.dream.generation.biomes;

import fr.openmc.core.utils.FastNoiseLite;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.CAVE_MATERIALS;

public class SoulForestChunkGenerator {

    public static final Material FOREST_SURFACE_MATERIAL = Material.SCULK;

    public static final FastNoiseLite terrainNoise = new FastNoiseLite();
    public static final FastNoiseLite detailNoise = new FastNoiseLite();

    public static void init(long seed) {
        terrainNoise.SetSeed((int) seed);
        terrainNoise.SetFrequency(0.003f);
        detailNoise.SetSeed((int) seed);
        detailNoise.SetFrequency(0.05f);

        terrainNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        terrainNoise.SetFractalOctaves(13);
    }

    public static void generateBlock(@NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.ChunkData chunkData, int x, int y, int z) {
        float noise2 = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 2) + (detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) / 10);
        float noise3 = detailNoise.GetNoise(x + (chunkX * 16), y, z + (chunkZ * 16));
        float currentY = (65 + (noise2 * 15));

        if (y >= currentY) return;

        float distanceToSurface = Math.abs(y - currentY); // The absolute y distance to the world surface.
        double function = .1 * Math.pow(distanceToSurface, 2) - 1; // A second grade polynomial offset to the noise max and min (1, -1).

        if (noise3 > Math.min(function, -.3)) {
            if (distanceToSurface < 3 && y > 63) {
                chunkData.setBlock(x, y, z, FOREST_SURFACE_MATERIAL);
            } else {
                chunkData.setBlock(x, y, z, CAVE_MATERIALS.get(random.nextInt(CAVE_MATERIALS.size())));
            }
        }
    }
}
