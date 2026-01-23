package fr.openmc.core.features.dream.generation;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator.MIN_HEIGHT_CLOUD;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MAX_HEIGHT_MUD;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MIN_HEIGHT_MUD;

public class DreamBiomeProvider extends BiomeProvider {
    private final PerlinNoiseGenerator noiseGenerator;
    private final List<Biome> biomes;
    private final int octaves = 5;
    private final double scale = 0.0025;

    public DreamBiomeProvider(long seed) {
        this.noiseGenerator = new PerlinNoiseGenerator(new Random(seed));

        this.biomes = new ArrayList<>();
        this.biomes.add(DreamBiome.SCULK_PLAINS.getBiome());
        this.biomes.add(DreamBiome.SOUL_FOREST.getBiome());
        this.biomes.add(DreamBiome.MUD_BEACH.getBiome());
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {

        if (y >= MIN_HEIGHT_CLOUD) {
            return DreamBiome.CLOUD_LAND.getBiome();
        }

        if (y <= MAX_HEIGHT_MUD && y > MIN_HEIGHT_MUD) {
            return DreamBiome.MUD_BEACH.getBiome();
        }

        if (y <= MIN_HEIGHT_MUD) {
            return DreamBiome.GLACITE_GROTTO.getBiome();
        }

        double noise = 0;
        double amplitude = 1;
        double frequency = 1;
        double maxValue = 0;

        for (int i = 0; i < octaves; i++) {
            noise += noiseGenerator.noise(x * scale * frequency, z * scale * frequency, 0.0) * amplitude;
            maxValue += amplitude;
            amplitude *= 0.5;
            frequency *= 2;
        }

        noise = noise / maxValue;

        List<Biome> landBiomes = biomes.stream()
                .filter(b -> b != DreamBiome.MUD_BEACH.getBiome())
                .toList();

        int biomeIndex = (int) ((noise + 1) * landBiomes.size() / 2) % landBiomes.size();
        return landBiomes.get(biomeIndex);
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return biomes;
    }
}