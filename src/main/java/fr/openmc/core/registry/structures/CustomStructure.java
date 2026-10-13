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
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.registry.structures.model.CustomStructureManager;
import fr.openmc.core.registry.structures.model.DBPlacedStructure;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Une structure réutilisable (ex: "maison"). Elle peut être posée autant de fois qu'on veut,
 * chaque placement étant identifié par son propre UUID.
 */
public class CustomStructure {

    // Format utilisé pour stocker les undos en DB
    private static final ClipboardFormat BACKUP_FORMAT = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC;

    private final OMCPlugin plugin = OMCPlugin.getInstance();
    @Getter
    private final String id;
    private final File file; // .schem / .schematic / .nbt

    public CustomStructure(String id, File file) {
        this.id = id;
        this.file = file;
    }

    // ------------------------------------------------------------------ PLACE

    /** Pose la structure avec un UUID de placement généré. */
    public CompletableFuture<UUID> place(Location location) {

        if (!CustomStructureManager.actionAllow()) return null;

        return place(UUID.randomUUID(), location);
    }

    /**
     * Pose la structure en lui donnant un identifiant UUID.
     * Échoue si cet UUID est déjà utilisé par un placement existant.
     * Peut être appelé depuis n'importe quel thread.
     *
     * @return le même UUID, une fois la structure posée et l'undo sauvegardé
     */
    public CompletableFuture<UUID> place(UUID placementId, Location location) {

        if (!CustomStructureManager.actionAllow()) return null;

        CompletableFuture<UUID> future = new CompletableFuture<>();
        final Location loc = location.clone();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (CustomStructureManager.existsPlacement(placementId)) {
                    future.completeExceptionally(new IllegalStateException(
                            "Le placement " + placementId + " existe déjà"));
                    return;
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                final DBPlacedStructure entry;
                try {
                    entry = pasteWithBackup(placementId, loc);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        CustomStructureManager.savePlacement(entry);
                        future.complete(placementId);
                    } catch (Exception e) {
                        OMCLogger.error("Structure posée mais undo non sauvegardé !", e);
                        future.completeExceptionally(e);
                    }
                });
            });
        });
        return future;
    }

    private DBPlacedStructure pasteWithBackup(UUID placementId, Location loc) throws Exception {

        if (!CustomStructureManager.actionAllow()) return null;

        Clipboard structure = readFile(file);
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(loc.getWorld());

        // Zone du monde qui sera écrasée
        BlockVector3 to = BukkitAdapter.asBlockVector(loc);
        BlockVector3 offset = to.subtract(structure.getOrigin());

        Region r = structure.getRegion();

        BlockVector3 min = r.getMinimumPoint().add(offset);
        BlockVector3 max = r.getMaximumPoint().add(offset);

        CuboidRegion area = new CuboidRegion(weWorld, min, max);

        try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
            // 1) sauvegarde de l'existant
            BlockArrayClipboard backup = new BlockArrayClipboard(area);
            ForwardExtentCopy copy = new ForwardExtentCopy(session, area, backup, min);
            copy.setCopyingEntities(false);
            Operations.complete(copy);

            // 2) collage
            Operations.complete(new ClipboardHolder(structure)
                    .createPaste(session)
                    .to(to)
                    .ignoreAirBlocks(false)
                    .build());

            return new DBPlacedStructure(placementId, id, loc.getWorld().getUID().toString(),
                    min.x(), min.y(), min.z(), serialize(backup));
        }
    }

    /** Retire UN placement précis grâce à son UUID.
     * @return false si introuvable.
     */
    public CompletableFuture<Boolean> remove(UUID placementId) {

        if (!CustomStructureManager.actionAllow()) return null;

        return removeInternal(() -> CustomStructureManager.getPlacement(placementId));
    }

    /** Retire le placement le plus récent de cette structure. */
    public CompletableFuture<Boolean> removeLast() {
        return removeInternal(() -> CustomStructureManager.getLatestPlacement(id));
    }

    /** Retire TOUS les placements de cette structure (du plus récent au plus ancien). @return le nombre retiré */
    public CompletableFuture<Integer> removeAll() {

        if (!CustomStructureManager.actionAllow()) return null;

        return list().thenCompose(placements -> {
            CompletableFuture<Integer> chain = CompletableFuture.completedFuture(0);
            for (DBPlacedStructure p : placements) {
                chain = chain.thenCompose(count ->
                        remove(p.getId()).thenApply(ok -> ok ? count + 1 : count));
            }
            return chain;
        });
    }

    /** Tous les placements de cette structure, du plus récent au plus ancien. */
    public CompletableFuture<List<DBPlacedStructure>> list() {
        CompletableFuture<List<DBPlacedStructure>> f = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try { f.complete(CustomStructureManager.getPlacements(id)); }
            catch (Exception e) { f.completeExceptionally(e); }
        });
        return f;
    }

    public CompletableFuture<Boolean> exists(UUID placementId) {
        CompletableFuture<Boolean> f = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try { f.complete(CustomStructureManager.existsPlacement(placementId)); }
            catch (Exception e) { f.completeExceptionally(e); }
        });
        return f;
    }

    private interface Lookup { DBPlacedStructure get() throws Exception; }

    private CompletableFuture<Boolean> removeInternal(Lookup lookup) {

        if (!CustomStructureManager.actionAllow()) return null;

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // 1) lecture DB
            final DBPlacedStructure placed;
            try {
                placed = lookup.get();
            } catch (Exception e) { future.completeExceptionally(e); return; }
            if (placed == null) { future.complete(false); return; }

            Bukkit.getScheduler().runTask(plugin, () -> {
                // 2) restauration (main thread)
                try {
                    World world = Bukkit.getWorld(UUID.fromString(placed.getWorld()));
                    if (world == null) throw new IllegalStateException("Monde introuvable");
                    restore(placed, world);
                } catch (Exception e) { future.completeExceptionally(e); return; }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    // 3) suppression de la ligne
                    try {
                        CustomStructureManager.deletePlacement(placed.getId());
                        future.complete(true);
                    } catch (Exception e) { future.completeExceptionally(e); }
                });
            });
        });
        return future;
    }

    private void restore(DBPlacedStructure placed, World world) throws Exception {

        if (!CustomStructureManager.actionAllow()) return;

        Clipboard backup = deserialize(placed.getBackup());
        backup.setOrigin(backup.getRegion().getMinimumPoint());
        BlockVector3 min = BlockVector3.at(placed.getX(), placed.getY(), placed.getZ());

        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            Operations.complete(new ClipboardHolder(backup)
                    .createPaste(session)
                    .to(min)
                    .ignoreAirBlocks(false) // remet aussi l'air
                    .build());
        }
    }

    /** Lit un .schem / .schematic via WorldEdit, ou un .nbt vanilla converti en clipboard WorldEdit. */
    private static Clipboard readFile(File file) throws IOException {

        if (!CustomStructureManager.actionAllow()) return null;

        if (file.getName().toLowerCase().endsWith(".nbt")) {
            return readNbt(file);
        }
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) throw new IOException("Format de schematic inconnu : " + file.getName());
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        }
    }

    /**
     * Convertit un .nbt (structure block) en clipboard WorldEdit, sans rien placer dans le monde.
     * Limites : le contenu des tile entities (coffres, panneaux...) et les entités ne sont pas conservés.
     */
    private static Clipboard readNbt(File file) throws IOException {

        if (!CustomStructureManager.actionAllow()) return null;

        Structure structure = Bukkit.getStructureManager().loadStructure(file);
        if (structure.getPaletteCount() == 0) throw new IOException("Structure vide : " + file.getName());

        BlockVector size = structure.getSize();
        CuboidRegion region = new CuboidRegion(
                BlockVector3.ZERO,
                BlockVector3.at(size.getBlockX() - 1, size.getBlockY() - 1, size.getBlockZ() - 1));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(BlockVector3.ZERO);

        try {
            for (org.bukkit.block.BlockState state : structure.getPalettes().get(0).getBlocks()) {
                BlockVector3 pos = BlockVector3.at(state.getX(), state.getY(), state.getZ());
                clipboard.setBlock(pos, BukkitAdapter.adapt(state.getBlockData()));
            }
        } catch (Exception e) {
            throw new IOException("Conversion du .nbt impossible : " + file.getName(), e);
        }
        return clipboard;
    }

    private static byte[] serialize(Clipboard clipboard) throws IOException {

        if (!CustomStructureManager.actionAllow()) return null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ClipboardWriter writer = BACKUP_FORMAT.getWriter(out)) {
            writer.write(clipboard);
        }
        return out.toByteArray();
    }

    private static Clipboard deserialize(byte[] data) throws IOException {

        if (!CustomStructureManager.actionAllow()) return null;

        try (ClipboardReader reader = BACKUP_FORMAT.getReader(new ByteArrayInputStream(data))) {
            return reader.read();
        }
    }
}