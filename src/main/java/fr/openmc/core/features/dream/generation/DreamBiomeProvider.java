package fr.openmc.core.features.dream.generation;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

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
    private final double scale = 0.0025; // Reduced scale for smoother transitions

    public DreamBiomeProvider(long seed) {
        this.noiseGenerator = new PerlinNoiseGenerator(new Random(seed));
        this.biomes = new ArrayList<>();
        this.biomes.add(DreamBiome.SCULK_PLAINS.getBiome());
        this.biomes.add(DreamBiome.SOUL_FOREST.getBiome());
        this.biomes.add(DreamBiome.MUD_BEACH.getBiome());
    }


    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
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
            noise += noiseGenerator.noise(x * scale * frequency, z * scale * frequency) * amplitude;
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
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return biomes;
    }

    private static class PerlinNoiseGenerator {
        private final Random random;
        private final int[] permutation;

        public PerlinNoiseGenerator(Random random) {
            this.random = random;
            this.permutation = new int[512];
            for (int i = 0; i < 256; i++) {
                permutation[i] = i;
            }
            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256 - i) + i;
                int temp = permutation[i];
                permutation[i] = permutation[j];
                permutation[j] = temp;
                permutation[i + 256] = permutation[i];
            }
        }

        public double noise(double x, double z) {
            int X = (int) Math.floor(x) & 255;
            int Z = (int) Math.floor(z) & 255;
            x -= Math.floor(x);
            z -= Math.floor(z);
            double u = fade(x);
            double w = fade(z);
            int A = permutation[X] + Z;
            int B = permutation[X + 1] + Z;
            return lerp(w, lerp(u, grad(permutation[A], x, z),
                            grad(permutation[B], x - 1, z)),
                    lerp(u, grad(permutation[A + 1], x, z - 1),
                            grad(permutation[B + 1], x - 1, z - 1)));
        }

        private double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : z;
            double v = h < 4 ? z : (h == 12 || h == 14 ? x : 0);
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}