package fr.openmc.core.features.dream.generation.populators;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VegetationPopulator extends BlockPopulator {

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        for (int iteration = 0; iteration < 10; iteration++) {
            int x = random.nextInt(16) + chunkX * 16;
            int z = random.nextInt(16) + chunkZ * 16;
            int y = CloudChunkGenerator.MIN_HEIGHT_CLOUD;
            while (limitedRegion.getType(x, y, z).isAir() && y > GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT) y--;

            Location loc = new Location(limitedRegion.getWorld(), x, y, z);
            if (!limitedRegion.getBiome(loc).equals(DreamBiome.SCULK_PLAINS.getBiome()) &&
                    !limitedRegion.getBiome(loc).equals(DreamBiome.SOUL_FOREST.getBiome())) return;

            if (limitedRegion.getType(x, y, z).isSolid()) {
                limitedRegion.setType(x, y + 1, z, Material.TRIPWIRE);
            }
        }
    }
}