package fr.openmc.core.utils.nms;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.Strategy;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PlayerBiomeNMS {
    private static Field SECTION_BIOMES;
    static {
        try {
            SECTION_BIOMES = LevelChunkSection.class.getDeclaredField("biomes");
            SECTION_BIOMES.setAccessible(true);
        } catch (Exception e) {
            OMCLogger.error(e.getMessage());
        }
    }

    public static void sendBiome(ServerPlayer nmsPlayer, Holder<Biome> biome, LevelChunk initialChunk) {
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();
        LevelChunk fakeChunk = PlayerBiomeNMS.getFakeChunk(
                nmsPlayer,
                initialChunk,
                nmsPlayer.level(),
                biome
        );
        ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk);
        biomeDataList.add(data);
        ClientboundChunksBiomesPacket packet = new ClientboundChunksBiomesPacket(biomeDataList);
        nmsPlayer.connection.send(packet);
    }

    public static void sendBiomes(Player player, Holder<Biome> biome) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel nmsWorld = nmsPlayer.level();

        int viewDistance = nmsWorld.getServer().getPlayerList().getViewDistance();
        ChunkPos center = nmsPlayer.chunkPosition();
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();
        for (int cx = center.x() - viewDistance; cx <= center.x() + viewDistance; cx++) {
            for (int cz = center.z() - viewDistance; cz <= center.z() + viewDistance; cz++) {
                LevelChunk chunk = nmsWorld.getChunkIfLoaded(cx, cz);
                if (chunk == null) continue;

                LevelChunk fakeChunk = PlayerBiomeNMS.getFakeChunk(nmsPlayer, chunk, nmsWorld, biome);

                ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk);
                biomeDataList.add(data);
            }
        }

        ClientboundChunksBiomesPacket packet = new ClientboundChunksBiomesPacket(biomeDataList);
        nmsPlayer.connection.send(packet);
    }

    public static LevelChunk getFakeChunk(ServerPlayer nmsPlayer, LevelChunk original, ServerLevel level, Holder<Biome> biome) {
        LevelChunk fakeChunk = new LevelChunk(level, original.getPos());

        LevelChunkSection[] originalSections = original.getSections();
        LevelChunkSection[] fakeSections = fakeChunk.getSections();

        for (int i = 0; i < originalSections.length; i++) {
            PalettedContainer<Holder<Biome>> container = new PalettedContainer<>(
                    biome,
                    Strategy.createForBiomes(nmsPlayer.level()
                            .registryAccess().lookupOrThrow(Registries.BIOME).asHolderIdMap()),
                    null
            );

            try {
                SECTION_BIOMES.set(fakeSections[i], container);
            } catch (IllegalAccessException e) {
                OMCLogger.error("Erreur d'acces à l'attribut biomes d'un levelChunkSetcion");
            }
        }

        return fakeChunk;
    }
}
