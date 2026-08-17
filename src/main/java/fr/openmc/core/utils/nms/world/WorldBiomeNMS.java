package fr.openmc.core.utils.nms.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftWorld;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class WorldBiomeNMS {
    public static void applyBiome(World world, Biome biome) throws IOException {
        File regionFolder = new File(world.getWorldFolder(), "region");
        File[] mcaFiles = regionFolder.listFiles();
        if (mcaFiles == null) return;

        for (File mcaFile : mcaFiles) {
            processRegionFile(world, mcaFile, biome.getKey().asString());
        }
    }

    private static void processRegionFile(World world, File mcaFile, String biomeId) throws IOException {
        ServerLevel nmsWorld = ((CraftWorld) world).getHandle();

        try (RegionFile region = new RegionFile(
                nmsWorld.moonrise$getChunkDataController().getCache().info(),
                mcaFile.toPath(),
                mcaFile.getParentFile().toPath(),
                false)) {
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    ChunkPos pos = new ChunkPos(x, z);
                    if (!region.hasChunk(pos)) continue;

                    CompoundTag chunkNbt;
                    try (DataInputStream in = region.getChunkDataInputStream(pos)) {
                        if (in == null) continue;
                        chunkNbt = NbtIo.read(in);
                    }

                    modifyBiomes(chunkNbt, biomeId);

                    try (DataOutputStream out = region.getChunkDataOutputStream(pos)) {
                        NbtIo.write(chunkNbt, out);
                    }
                }
            }
        }
    }

    private static void modifyBiomes(CompoundTag chunkNbt, String biomeId) {
        ListTag sections = chunkNbt.getList("sections").orElseThrow();
        for (int i = 0; i < sections.size(); i++) {
            CompoundTag section = sections.getCompound(i).orElseThrow();
            if (!section.contains("biomes")) continue;

            CompoundTag biomes = section.getCompound("biomes").orElseThrow();
            biomes.remove("data");
            ListTag palette = new ListTag();
            palette.add(StringTag.valueOf(biomeId));
            biomes.put("palette", palette);
            section.put("biomes", biomes);
        }
    }
}
