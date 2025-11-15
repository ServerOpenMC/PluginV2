package fr.openmc.core.features.dream.generation.structures.soulforest;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructurePopulator;
import fr.openmc.core.utils.structure.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SoulAltarStructure extends DreamStructurePopulator {

    private static final double BASE_CAMP_PROBABILITY = 0.005;
    public static final String STRUCTURE_NAME = "soul_altar";

    public SoulAltarStructure() {
        super("dream_structures", List.of(STRUCTURE_NAME));
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (!DreamUtils.isDreamWorld(world)) return;
        if (random.nextDouble() >= BASE_CAMP_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.SOUL_FOREST.getBiome())) return;

        SchematicsUtils.CachedSchematic schematic = getRandomSchematic(random);

        placeAndRegisterSchematic(schematic, loc, DreamStructure.DreamType.fromId(STRUCTURE_NAME), true);
    }
}