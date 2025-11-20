package fr.openmc.core.features.dream.generation.biomes;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.FastNoiseLite;
import org.bukkit.Material;
import org.bukkit.block.data.type.Snow;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.DreamChunkGenerator.FLOOR_MATERIAL;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MIN_HEIGHT_MUD;

public class GlaciteCaveChunkGenerator {

    private static final List<Material> MINERALS = Arrays.asList(
            Material.DEEPSLATE_COAL_ORE
    );
    public static final List<Material> CAVE_MATERIALS = Arrays.asList(
            Material.DEEPSLATE,
            Material.SMOOTH_BASALT
    );

    private static final Material SURFACE_MATERIAL = Material.SNOW_BLOCK;
    private static final int NUMBER_SURFACE_BLOCK = 2;

    public static final int MAX_CAVE_HEIGHT = 64;
    public static final int MIN_CAVE_HEIGHT = -64;

    private static final FastNoiseLite noiseA = new FastNoiseLite();
    private static final FastNoiseLite noiseB = new FastNoiseLite();

    public static void init(long seed) {
        noiseA.SetSeed((int) seed);
        noiseA.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noiseA.SetFractalType(FastNoiseLite.FractalType.Ridged);
        noiseA.SetFrequency(0.006f);
        noiseA.SetFractalOctaves(3);

        noiseB.SetSeed((int) seed);
        noiseB.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noiseB.SetFractalType(FastNoiseLite.FractalType.Ridged);
        noiseB.SetFrequency(0.006f);
        noiseB.SetFractalOctaves(3);
    }

    public static void generateBlock(@NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.ChunkData chunkData, int x, int y, int z) {
        if (y == MIN_CAVE_HEIGHT) {
            chunkData.setBlock(x, y, z, FLOOR_MATERIAL);
            return;
        }

        int worldX = (chunkX << 4) + x;
        int worldZ = (chunkZ << 4) + z;

        if (chunkData.getBiome(x, y, z) == DreamBiome.MUD_BEACH.getBiome()) {
            if (y > MIN_HEIGHT_MUD) return;
        }

        if (y < MIN_CAVE_HEIGHT || y > MAX_CAVE_HEIGHT) return;

        double vA = noiseA.GetNoise(worldX, y, worldZ);
        double vB = noiseB.GetNoise(worldX, y, worldZ);

        // petite valeur, petit tunnel - grosse valeur, gros tunnel
        double threshold = 0.42;
        boolean isCave = (vA * vA + vB * vB) < threshold * threshold;

        if (!isCave) {
            Material wallMat = CAVE_MATERIALS.get(random.nextInt(CAVE_MATERIALS.size()));
            if (random.nextFloat() < 0.01) {
                wallMat = MINERALS.get(random.nextInt(MINERALS.size()));
            }
            chunkData.setBlock(x, y, z, wallMat);
            return;
        }

        chunkData.setBlock(x, y, z, Material.AIR);

        if (y - 4 >= MIN_CAVE_HEIGHT) {
            Material below = chunkData.getType(x, y - 1, z);
            if (below != Material.AIR && below != Material.SNOW && below != SURFACE_MATERIAL) {

                for (int i = 0; i < NUMBER_SURFACE_BLOCK; i++) {
                    int snowY = y - 1 + i;
                    if (snowY <= MAX_CAVE_HEIGHT) {
                        chunkData.setBlock(x, snowY, z, SURFACE_MATERIAL);
                    }
                }

                int solidCount = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        Material around = chunkData.getType(x + dx, y - 1, z + dz);
                        if (around.isSolid())
                            solidCount++;
                    }
                }

                int layers;
                if (solidCount >= 6) {
                    layers = 6;
                } else if (solidCount >= 3) {
                    layers = 4;
                } else {
                    layers = 2;
                }

                Snow snowData = (Snow) Material.SNOW.createBlockData();
                snowData.setLayers(layers);
                chunkData.setBlock(x, y, z, snowData);
            }
        }
    }
}