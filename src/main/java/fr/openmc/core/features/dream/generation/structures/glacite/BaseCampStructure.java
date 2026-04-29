package fr.openmc.core.features.dream.generation.structures.glacite;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructurePopulator;
import fr.openmc.core.utils.structure.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class BaseCampStructure extends DreamStructurePopulator {

    private static final double BASE_CAMP_PROBABILITY = 0.001;
    public static final String STRUCTURE_NAME = "base_camp";

    public BaseCampStructure() {
        super("dream_structures", List.of(STRUCTURE_NAME));
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (!DreamUtils.isDreamWorld(world)) return;
        if (random.nextDouble() >= BASE_CAMP_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT + 1;

        Location loc = new Location(world, x, y, z);

        SchematicsUtils.CachedSchematic schematic = getRandomSchematic(random);

        placeAndRegisterSchematic(schematic, loc, DreamStructure.DreamType.fromId(STRUCTURE_NAME), false);
    }
}