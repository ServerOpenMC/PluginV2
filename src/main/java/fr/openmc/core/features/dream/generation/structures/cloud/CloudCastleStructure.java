package fr.openmc.core.features.dream.generation.structures.cloud;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructurePopulator;
import fr.openmc.core.utils.structure.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class CloudCastleStructure extends DreamStructurePopulator {

    private static final double CLOUD_CASTLE_PROBABILITY = 0.0007;
    public static final String STRUCTURE_NAME = "cloud_castle";

    public CloudCastleStructure() {
        super("dream_structures", List.of(STRUCTURE_NAME));
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (!world.getName().equalsIgnoreCase(DreamDimensionManager.DIMENSION_NAME)) return;
        if (random.nextDouble() >= CLOUD_CASTLE_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = CloudChunkGenerator.MAX_HEIGHT_CLOUD;

        Location origin = new Location(world, x, y, z);

        SchematicsUtils.CachedSchematic schematic = getRandomSchematic(random);

        placeAndRegisterSchematic(schematic, origin, DreamStructure.DreamType.fromId(STRUCTURE_NAME), false);
    }
}