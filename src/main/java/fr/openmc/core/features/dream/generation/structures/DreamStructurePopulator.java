package fr.openmc.core.features.dream.generation.structures;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.structure.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class DreamStructurePopulator extends BlockPopulator {

    protected final String structureGroup;
    protected final List<String> schematics;

    public DreamStructurePopulator(String structureGroup, List<String> schematics) {
        this.structureGroup = structureGroup;
        this.schematics = schematics;

        Map<String, List<String>> toPreload = Map.of(structureGroup, schematics);
        SchematicsUtils.preloadSchematics(toPreload);
    }

    @Override
    public abstract void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk);

    protected SchematicsUtils.CachedSchematic getRandomSchematic(Random random) {
        if (schematics.isEmpty()) return null;
        String name = schematics.get(random.nextInt(schematics.size()));
        return SchematicsUtils.getCachedSchematic(structureGroup, name);
    }

    protected void placeAndRegisterSchematic(SchematicsUtils.CachedSchematic schematic, Location origin, DreamStructure.DreamType type, boolean checkFloating) {
        if (schematic == null) return;

        World world = origin.getWorld();
        Clipboard clipboard = schematic.clipboard();
        ClipboardHolder holder = new ClipboardHolder(clipboard);

        boolean success = SchematicsUtils.pasteSchem(world, schematic, origin, checkFloating);
        if (!success) return;

        Region region = clipboard.getRegion();
        BlockVector3 clipboardOffset = region.getMinimumPoint().subtract(clipboard.getOrigin());

        Vector3 to = BukkitAdapter.asBlockVector(origin).toVector3();
        Vector3 realMin = to.add(holder.getTransform().apply(clipboardOffset.toVector3()));
        Vector3 realMax = realMin.add(holder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));

        BlockVector3 min = BlockVector3.at(
                Math.min(realMin.x(), realMax.x()),
                Math.min(realMin.y(), realMax.y()),
                Math.min(realMin.z(), realMax.z())
        );
        BlockVector3 max = BlockVector3.at(
                Math.max(realMin.x(), realMax.x()),
                Math.max(realMin.y(), realMax.y()),
                Math.max(realMin.z(), realMax.z())
        );
        DreamStructuresManager.addStructure(type, min, max);

        OMCPlugin.getInstance().getSLF4JLogger().info("Structure '{}' placée entre {}, {}, {} et {}, {}, {}", type.getId(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }
}
