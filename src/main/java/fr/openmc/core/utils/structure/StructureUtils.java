package fr.openmc.core.utils.structure;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import fr.openmc.core.OMCPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class StructureUtils {
    private static final Map<String, CachedStructure> STRUCTURE_CACHE = new HashMap<>();

    public record CachedStructure(
            CompoundTag nbt,
            int[] size,
            BlockData[] palette,
            List<int[]> blocksToPlace
    ) {
    }

    public static void preloadStructures(Map<String, List<String>> structuresByGroup) {
        for (Map.Entry<String, List<String>> entry : structuresByGroup.entrySet()) {
            String group = entry.getKey();
            for (String name : entry.getValue()) {
                try {
                    CompoundTag nbt = loadNBT(group, name);
                    CachedStructure cached = buildCache(nbt);
                    STRUCTURE_CACHE.put(group + "/" + name.replace(".nbt", ""), cached);
                } catch (IOException e) {
                    OMCPlugin.getInstance().getSLF4JLogger().error("Impossible de précharger la structure {}/{}", group, name);
                }
            }
        }
    }

    private static CompoundTag loadNBT(String group, String name) throws IOException {
        String path = "structures/" + group + "/" + name.replace(".nbt", "") + ".nbt";
        try (InputStream in = OMCPlugin.getInstance().getResource(path)) {
            if (in == null) throw new IllegalArgumentException("Structure introuvable : " + path);
            try (NBTInputStream nbtIn = new NBTInputStream(in)) {
                Tag base = nbtIn.readTag();
                if (!(base instanceof CompoundTag compound)) {
                    throw new IllegalStateException("Structure NBT invalide : " + path);
                }
                return compound;
            }
        }
    }

    private static CachedStructure buildCache(CompoundTag nbt) {
        CompoundMap compound = nbt.getValue();

        ListTag paletteList = (ListTag) compound.get("palette");
        BlockData[] states = new BlockData[paletteList.getValue().size()];
        for (int i = 0; i < states.length; i++) {
            CompoundMap blockTag = ((CompoundTag) paletteList.getValue().get(i)).getValue();
            StringBuilder s = new StringBuilder(blockTag.get("Name").getValue().toString());
            if (blockTag.containsKey("Properties")) {
                CompoundMap props = ((CompoundTag) blockTag.get("Properties")).getValue();
                if (!props.isEmpty()) {
                    s.append("[");
                    int k = 0;
                    for (Map.Entry<String, Tag<?>> e : props.entrySet()) {
                        if (k++ > 0) s.append(",");
                        s.append(e.getKey()).append("=").append(e.getValue().getValue());
                    }
                    s.append("]");
                }
            }
            states[i] = Bukkit.createBlockData(s.toString());
        }

        ListTag blocksList = (ListTag) compound.get("blocks");
        List<int[]> blocksToPlace = new ArrayList<>();
        for (Object oTag : blocksList.getValue()) {
            CompoundMap blockTag = ((CompoundTag) oTag).getValue();
            ListTag posListTag = (ListTag) blockTag.get("pos");
            int x = ((IntTag) posListTag.getValue().get(0)).getValue();
            int y = ((IntTag) posListTag.getValue().get(1)).getValue();
            int z = ((IntTag) posListTag.getValue().get(2)).getValue();
            int stateIdx = ((IntTag) blockTag.get("state")).getValue();
            blocksToPlace.add(new int[]{x, y, z, stateIdx});
        }

        ListTag sizeList = (ListTag) compound.get("size");
        int[] size = new int[]{
                ((IntTag) sizeList.getValue().get(0)).getValue(),
                ((IntTag) sizeList.getValue().get(1)).getValue(),
                ((IntTag) sizeList.getValue().get(2)).getValue()
        };

        return new CachedStructure(nbt, size, states, blocksToPlace);
    }

    public static CachedStructure getCachedStructure(String group, String name) {
        return STRUCTURE_CACHE.get(group + "/" + name.replace(".nbt", ""));
    }

    /**
     * Places a structure from an NBT file into the world at the given location.
     * Structure files can be exported using a Minecraft Structure Block.
     * <p>
     * Optimisations :
     * - Lecture du cache des structures
     * - Lecture rapide des Chunks (fait avec IA)
     * - Réduction des appels à world.getBlockAt() / getBlockData().
     *
     * @param cached  the NBT structure.
     * @param target  The lowest (min corner) location where to place the structure.
     * @param mirrorX Whether to mirror the structure on the X axis (ignores block rotation).
     * @param mirrorZ Whether to mirror the structure on the Z axis (ignores block rotation).
     * @throws IOException If the NBT file is malformed or unreadable.
     */
    public static void placeStructure(CachedStructure cached, Location target, boolean mirrorX, boolean mirrorZ, boolean placeAir) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                List<int[]> originalBlocks = cached.blocksToPlace();
                int[] size = cached.size();
                BlockData[] states = cached.palette();

                List<int[]> blocksToPlace = new ArrayList<>(originalBlocks.size());
                final List<int[]> baseSolidCells = new ArrayList<>();
                for (int[] e : originalBlocks) {
                    int x = e[0], y = e[1], z = e[2], stateIdx = e[3];
                    if (mirrorX) x = (size[0] - 1) - x;
                    if (mirrorZ) z = (size[2] - 1) - z;
                    blocksToPlace.add(new int[]{x, y, z, stateIdx});
                    if (y == 0 && states[stateIdx].getMaterial().isSolid()) {
                        baseSolidCells.add(new int[]{x, z});
                    }
                }

                World world = target.getWorld();
                int baseX = target.getBlockX();
                int baseY = target.getBlockY();
                int baseZ = target.getBlockZ();

                int chunkMinX = (baseX) >> 4;
                int chunkMaxX = (baseX + size[0]) >> 4;
                int chunkMinZ = (baseZ) >> 4;
                int chunkMaxZ = (baseZ + size[2]) >> 4;

                List<CompletableFuture<Chunk>> futures = new ArrayList<>();
                for (int cx = chunkMinX; cx <= chunkMaxX; cx++) {
                    for (int cz = chunkMinZ; cz <= chunkMaxZ; cz++) {
                        futures.add(world.getChunkAtAsync(cx, cz, true));
                    }
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                    Map<Long, Chunk> loadedChunks = new HashMap<>();
                    for (CompletableFuture<Chunk> f : futures) {
                        try {
                            Chunk c = f.join();
                            long key = (((long) c.getX()) << 32) | (c.getZ() & 0xffffffffL);
                            loadedChunks.put(key, c);
                        } catch (CompletionException ignored) {

                        }
                    }

                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                        ServerLevel handle = ((CraftWorld) world).getHandle();

                        Map<Long, ChunkSnapshot> snapshots = new HashMap<>();
                        int floating = 0;
                        int checked = 0;
                        for (int i = 0; i < baseSolidCells.size(); i += 3) {
                            int[] rc = baseSolidCells.get(i);
                            int worldX = baseX + rc[0];
                            int worldZ = baseZ + rc[1];
                            int chunkX = worldX >> 4;
                            int chunkZ = worldZ >> 4;
                            long key = (((long) chunkX) << 32) | (chunkZ & 0xffffffffL);

                            Chunk chunk = loadedChunks.get(key);
                            if (chunk == null || !chunk.isLoaded()) {
                                floating++;
                                checked++;
                                continue;
                            }

                            ChunkSnapshot snap = snapshots.get(key);
                            if (snap == null) {
                                snap = chunk.getChunkSnapshot();
                                snapshots.put(key, snap);
                            }
                            int localX = Math.floorMod(worldX, 16);
                            int localZ = Math.floorMod(worldZ, 16);

                            if (baseY - 1 < world.getMinHeight()) {
                                floating++;
                                checked++;
                                continue;
                            }

                            Material mat = snap.getBlockType(localX, baseY - 1, localZ);
                            if (mat.isAir() || !mat.isSolid()) {
                                floating++;
                            }
                            checked++;
                        }

                        if (checked > 0 && ((double) floating / checked) > 0.40D) {
                            return;
                        }

                        final int batchSize = 2000;
                        new BukkitRunnable() {
                            int index = 0;

                            @Override
                            public void run() {
                                int placed = 0;
                                while (index < blocksToPlace.size() && placed < batchSize) {
                                    int[] e = blocksToPlace.get(index++);
                                    BlockPos pos = new BlockPos(baseX + e[0], baseY + e[1], baseZ + e[2]);
                                    BlockData data = states[e[3]];

                                    if (!placeAir && data.getMaterial().isAir()) continue;

                                    handle.setBlock(pos, ((CraftBlockData) data).getState(), 2 | 16);
                                    placed++;
                                }
                                if (index >= blocksToPlace.size()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimer(OMCPlugin.getInstance(), 1L, 1L);
                    });
                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
