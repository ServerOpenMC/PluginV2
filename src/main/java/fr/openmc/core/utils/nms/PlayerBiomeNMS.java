package fr.openmc.core.utils.nms;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.Identifier;
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

    /**
     * Envoie un simple biome sur un chunk
     * @param nmsPlayer Le joueur à envoyer les changements
     * @param biome le biome a envoyer
     * @param initialChunk le chunk a remplacer le biome
     */
    public static void sendBiome(ServerPlayer nmsPlayer, Holder<Biome> biome, LevelChunk initialChunk) {
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();
        LevelChunk fakeChunk = PlayerBiomeNMS.getFakeChunk(
                initialChunk,
                nmsPlayer.level(),
                biome
        );
        ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk);
        biomeDataList.add(data);
        ClientboundChunksBiomesPacket packet = new ClientboundChunksBiomesPacket(biomeDataList);
        nmsPlayer.connection.send(packet);
    }

    /**
     * Actualise tout les biomes au alentour (dans la view distance du joueur)
     * @param player le joueur a envoyer le changement de biome
     * @param biome le biome a afficher
     */
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

                LevelChunk fakeChunk = PlayerBiomeNMS.getFakeChunk(chunk, nmsWorld, biome);

                ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk);
                biomeDataList.add(data);
            }
        }

        ClientboundChunksBiomesPacket packet = new ClientboundChunksBiomesPacket(biomeDataList);
        nmsPlayer.connection.send(packet);
    }

    /**
     * Remplace un simple biome par
     * @param nmsPlayer Le joueur à envoyer les changements
     * @param namespace le namespace du biome a envoyer
     * @param id le id du biome a envoyer
     * @param initialChunk le chunk a remplacer le biome
     */
    public static void remplaceBiome(ServerPlayer nmsPlayer, LevelChunk initialChunk, String namespace, String id) {
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();
        LevelChunk fakeChunk = PlayerBiomeNMS.getFakeChunkWithMapping(
                initialChunk,
                nmsPlayer.level(),
                namespace,
                id
        );
        ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk);
        biomeDataList.add(data);
        ClientboundChunksBiomesPacket packet = new ClientboundChunksBiomesPacket(biomeDataList);
        nmsPlayer.connection.send(packet);
    }

    public static void replaceBiomes(ServerPlayer nmsPlayer, String namespace, String id) {
        ServerLevel nmsWorld = nmsPlayer.level();

        int viewDistance = nmsWorld.getServer().getPlayerList().getViewDistance();
        ChunkPos center = nmsPlayer.chunkPosition();
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();

        for (int cx = center.x() - viewDistance; cx <= center.x() + viewDistance; cx++) {
            for (int cz = center.z() - viewDistance; cz <= center.z() + viewDistance; cz++) {
                LevelChunk chunk = nmsWorld.getChunkIfLoaded(cx, cz);
                if (chunk == null) continue;

                LevelChunk fakeChunk = getFakeChunkWithMapping(chunk, nmsWorld, namespace, id);
                biomeDataList.add(new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk));
            }
        }

        nmsPlayer.connection.send(new ClientboundChunksBiomesPacket(biomeDataList));
    }

    /**
     * Crée un faux chunk en se basant sur l'originel et en changeant juste le biome du chunk
     * @param original le chunk original
     * @param level le monde ou y'a le chunk
     * @param biome le biome a injecter dans le faux chunk
     * @return le faux chunk avec le biome
     */
    public static LevelChunk getFakeChunk(LevelChunk original, ServerLevel level, Holder<Biome> biome) {
        LevelChunk fakeChunk = new LevelChunk(level, original.getPos());

        LevelChunkSection[] originalSections = original.getSections();
        LevelChunkSection[] fakeSections = fakeChunk.getSections();

        for (int i = 0; i < originalSections.length; i++) {
            PalettedContainer<Holder<Biome>> container = new PalettedContainer<>(
                    biome,
                    Strategy.createForBiomes(level.registryAccess().lookupOrThrow(Registries.BIOME).asHolderIdMap()),
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

    private static LevelChunk getFakeChunkWithMapping(
            LevelChunk original,
            ServerLevel nmsWorld,
            String namespace,
            String id) {

        LevelChunk fake = new LevelChunk(nmsWorld, original.getPos());
        LevelChunkSection[] originalSections = original.getSections();
        LevelChunkSection[] fakeSections = fake.getSections();

        for (int i = 0; i < originalSections.length; i++) {
            LevelChunkSection originalSection = originalSections[i];

            PalettedContainer<Holder<Biome>> fakeBiomes = new PalettedContainer<>(
                    originalSection.getNoiseBiome(0,0,0),
                    Strategy.createForBiomes(nmsWorld.registryAccess().lookupOrThrow(Registries.BIOME).asHolderIdMap()),
                    null
            );

            Registry<Biome> registry = nmsWorld.registryAccess().lookupOrThrow(Registries.BIOME);

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    for (int z = 0; z < 4; z++) {
                        Holder<Biome> originalBiome = originalSection.getNoiseBiome(x, y, z);
                        // todo: faire directement in Identifier
                        String keyMapped = namespace + ":" +
                                originalBiome.unwrapKey().orElseThrow().identifier().getPath() + "_" + id;

                        Holder<Biome> mapped = registry.get(Identifier.parse(keyMapped)).orElseThrow(() ->
                                new IllegalStateException("Biome " + keyMapped + " introuvable dans le registre")
                        );

                        fakeBiomes.set(x, y, z, mapped);
                    }
                }
            }

            fakeSections[i] = new LevelChunkSection(originalSection.getStates(), fakeBiomes);
        }

        return fake;
    }
}
