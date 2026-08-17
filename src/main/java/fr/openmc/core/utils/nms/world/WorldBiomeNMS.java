package fr.openmc.core.utils.nms.world;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.Strategy;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import java.lang.reflect.Field;

public class WorldBiomeNMS {
    private static Field SECTION_BIOMES;
    static {
        try {
            SECTION_BIOMES = LevelChunkSection.class.getDeclaredField("biomes");
            SECTION_BIOMES.setAccessible(true);
        } catch (Exception e) {
            OMCLogger.error(e.getMessage());
        }
    }

    public static void setChunkBiome(LevelChunk chunk, Holder<Biome> biome) {
        ServerLevel level = (ServerLevel) chunk.getLevel();
        Strategy<Holder<Biome>> idMap = Strategy.createForBiomes(
                level.registryAccess().lookupOrThrow(Registries.BIOME).asHolderIdMap()
        );

        for (LevelChunkSection section : chunk.getSections()) {
            PalettedContainer<Holder<Biome>> container = new PalettedContainer<>(biome, idMap, null);
            try {
                SECTION_BIOMES.set(section, container);
            } catch (IllegalAccessException e) {
                OMCLogger.error("Erreur d'accès à l'attribut biomes d'un LevelChunkSection");
            }
        }

        chunk.markUnsaved();
    }

    public static void setWorldBiome(World world, int minChunkX, int maxChunkX, int minChunkZ, int maxChunkZ, Holder<Biome> biome) {
        ServerLevel level = ((CraftWorld) world).getHandle();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!world.isChunkGenerated(cx, cz)) continue;

                LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, true);
                if (chunk == null) continue;

                setChunkBiome(chunk, biome);

                world.unloadChunk(cx, cz, true);
            }
        }
    }
}
