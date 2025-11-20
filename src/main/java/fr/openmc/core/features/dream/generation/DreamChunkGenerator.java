package fr.openmc.core.features.dream.generation;

import fr.openmc.core.features.dream.generation.biomes.*;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;

public class DreamChunkGenerator extends ChunkGenerator {
    public static final Material FLOOR_MATERIAL = Material.BEDROCK;
    private final DreamBiomeProvider biomeProvider;

    public DreamChunkGenerator(long seed) {
        this.biomeProvider = new DreamBiomeProvider(seed);
    }

    /* https://www.spigotmc.org/threads/545616/ */

    @Override
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return biomeProvider;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        for (int y = chunkData.getMinHeight(); y < 130 && y < chunkData.getMaxHeight(); y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Biome biome = chunkData.getBiome(x, y, z);

                    if (biome.equals(DreamBiome.SCULK_PLAINS.getBiome())) {
                        PlainsChunkGenerator.generateBlock(random, chunkX, chunkZ, chunkData, x, y, z);
                    } else if (biome.equals(DreamBiome.SOUL_FOREST.getBiome())) {
                        SoulForestChunkGenerator.generateBlock(random, chunkX, chunkZ, chunkData, x, y, z);
                    } else if (biome.equals(DreamBiome.MUD_BEACH.getBiome())) {
                        MudBeachChunkGenerator.generateBlock(random, chunkX, chunkZ, chunkData, x, y, z);
                    } else if (biome.equals(DreamBiome.CLOUD_LAND.getBiome())) {
                        CloudChunkGenerator.generateBlock(random, chunkX, chunkZ, chunkData, x, y, z);
                    }
                }
            }
        }

        for (int y = MIN_CAVE_HEIGHT; y < MAX_CAVE_HEIGHT; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    GlaciteCaveChunkGenerator.generateBlock(random, chunkX, chunkZ, chunkData, x, y, z);
                }
            }
        }

        // sol avant la bedrock
        for (int y = MIN_CAVE_HEIGHT + 1; y < MIN_CAVE_HEIGHT + 4; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (y == MIN_CAVE_HEIGHT + 1) {
                        if (chunkData.getType(x, y, z).isAir())
                            chunkData.setBlock(x, y, z, Material.BLUE_ICE);
                    } else {
                        if (chunkData.getType(x, y, z).isAir())
                            chunkData.setBlock(x, y, z, Material.ICE);
                    }
                }
            }
        }
    }
}