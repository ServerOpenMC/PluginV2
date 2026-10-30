package fr.openmc.core.registry.structures;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.openmc.core.registry.structures.model.CustomStructureManager;
import fr.openmc.core.registry.structures.model.DBPlacedStructure;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Represents a reusable structure that can be placed multiple times.
 * All public methods must be called on the server's main thread.
 */
public class CustomStructure {

    private static final ClipboardFormat BACKUP_FORMAT = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC;

    @Getter
    private final String id;
    private final File file;

    public CustomStructure(String id, File file) {
        this.id = id;
        this.file = file;
    }

    /**
     * Places this structure and generates a unique ID for the placement.
     */
    public UUID place(Location location) throws Exception {
        return place(UUID.randomUUID(), location);
    }

    /**
     * Places this structure using the provided placement ID.
     * An ID that already exists in the database is rejected.
     */
    public UUID place(UUID placementId, Location location) throws Exception {
        requireMainThread();

        if (!CustomStructureManager.actionAllow()) {
            throw new IllegalStateException("WorldEdit or FastAsyncWorldEdit is not enabled.");
        }
        if (placementId == null || location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Placement ID, location and world must not be null.");
        }
        if (CustomStructureManager.existsPlacement(placementId)) {
            throw new IllegalStateException("A structure placement with ID " + placementId + " already exists.");
        }

        Clipboard structure = readStructure(file);
        World world = location.getWorld();
        BlockVector3 target = BukkitAdapter.asBlockVector(location);
        BlockVector3 offset = target.subtract(structure.getOrigin());
        BlockVector3 min = structure.getRegion().getMinimumPoint().add(offset);
        BlockVector3 max = structure.getRegion().getMaximumPoint().add(offset);

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        CuboidRegion affectedRegion = new CuboidRegion(weWorld, min, max);
        byte[] backupData;

        try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
            BlockArrayClipboard backup = new BlockArrayClipboard(affectedRegion);
            backup.setOrigin(min);

            ForwardExtentCopy copy = new ForwardExtentCopy(session, affectedRegion, backup, min);
            copy.setCopyingEntities(false);
            Operations.complete(copy);

            backupData = serialize(backup);

            Operations.complete(new ClipboardHolder(structure)
                    .createPaste(session)
                    .to(target)
                    .ignoreAirBlocks(false)
                    .build());
        }

        DBPlacedStructure placement = new DBPlacedStructure(
                placementId,
                id,
                world.getUID().toString(),
                min.x(),
                min.y(),
                min.z(),
                backupData
        );

        try {
            CustomStructureManager.savePlacement(placement);
        } catch (Exception exception) {
            // Roll back the paste if the placement could not be recorded.
            restoreBackup(world, min, backupData);
            throw exception;
        }

        return placementId;
    }

    /**
     * Restores the blocks that were present before the specified placement.
     *
     * @return true if a placement was found and restored, otherwise false
     */
    public boolean remove(UUID placementId) throws Exception {
        requireMainThread();

        if (!CustomStructureManager.actionAllow()) {
            throw new IllegalStateException("WorldEdit or FastAsyncWorldEdit is not enabled.");
        }

        DBPlacedStructure placement = CustomStructureManager.getPlacement(placementId);
        if (placement == null || !id.equals(placement.getStructureId())) {
            return false;
        }

        World world = Bukkit.getWorld(UUID.fromString(placement.getWorld()));
        if (world == null) {
            throw new IllegalStateException("World not found: " + placement.getWorld());
        }

        BlockVector3 min = BlockVector3.at(placement.getX(), placement.getY(), placement.getZ());
        restoreBackup(world, min, placement.getBackup());
        CustomStructureManager.deletePlacement(placementId);
        return true;
    }

    private void restoreBackup(World world, BlockVector3 min, byte[] data) throws Exception {
        Clipboard backup = deserialize(data);
        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            Operations.complete(new ClipboardHolder(backup)
                    .createPaste(session)
                    .to(min)
                    .ignoreAirBlocks(false)
                    .build());
        }
    }

    private static Clipboard readStructure(File file) throws IOException {
        if (file == null || !file.isFile()) {
            throw new IOException("Structure file not found: " + file);
        }

        if (file.getName().toLowerCase().endsWith(".nbt")) {
            return readNbt(file);
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IOException("Unsupported structure format: " + file.getName());
        }

        try (FileInputStream input = new FileInputStream(file);
             ClipboardReader reader = format.getReader(input)) {
            return reader.read();
        }
    }

    /**
     * Converts a vanilla structure-block NBT file into a WorldEdit clipboard.
     * This basic conversion copies block data only, not entities or block-entity contents.
     */
    private static Clipboard readNbt(File file) throws IOException {
        Structure structure = Bukkit.getStructureManager().loadStructure(file);
        BlockArrayClipboard clipboard = getBlockArrayClipboard(file, structure);

        try {
            for (BlockState state : structure.getPalettes().getFirst().getBlocks()) {
                BlockVector3 position = BlockVector3.at(state.getX(), state.getY(), state.getZ());
                clipboard.setBlock(position, BukkitAdapter.adapt(state.getBlockData()));
            }
        } catch (Exception exception) {
            throw new IOException("Could not convert NBT structure: " + file.getName(), exception);
        }

        return clipboard;
    }

    private static @NotNull BlockArrayClipboard getBlockArrayClipboard(File file, Structure structure) throws IOException {
        if (structure.getPaletteCount() == 0) {
            throw new IOException("Structure is empty: " + file.getName());
        }

        BlockVector size = structure.getSize();
        BlockVector3 min = BlockVector3.ZERO;
        BlockVector3 max = BlockVector3.at(
                size.getBlockX() - 1,
                size.getBlockY() - 1,
                size.getBlockZ() - 1
        );

        BlockArrayClipboard clipboard = new BlockArrayClipboard(new CuboidRegion(min, max));
        clipboard.setOrigin(BlockVector3.ZERO);
        return clipboard;
    }

    private static byte[] serialize(Clipboard clipboard) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ClipboardWriter writer = BACKUP_FORMAT.getWriter(output)) {
            writer.write(clipboard);
        }
        return output.toByteArray();
    }

    private static Clipboard deserialize(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            throw new IOException("The placement backup is empty.");
        }

        try (ByteArrayInputStream input = new ByteArrayInputStream(data);
             ClipboardReader reader = BACKUP_FORMAT.getReader(input)) {
            return reader.read();
        }
    }

    private static void requireMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("CustomStructure methods must be called on the server main thread.");
        }
    }
}
