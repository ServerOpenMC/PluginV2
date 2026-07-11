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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
     * Remplace un simple biome par un id de biome transformé par identifierModifier
     * @param nmsPlayer Le joueur à envoyer les changements
     * @param identifierModifier la clé du biome a modifier
     * @param initialChunk le chunk a remplacer le biome
     */
    public static void remplaceBiome(
            ServerPlayer nmsPlayer,
            LevelChunk initialChunk,
            String keyMappedBiome,
            Function<Identifier, Identifier> identifierModifier) {
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();
        LevelChunk fakeChunk = PlayerBiomeNMS.getFakeChunkWithMapping(
                initialChunk,
                keyMappedBiome,
                identifierModifier
        );
        ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(fakeChunk);
        biomeDataList.add(data);
        ClientboundChunksBiomesPacket packet = new ClientboundChunksBiomesPacket(biomeDataList);
        nmsPlayer.connection.send(packet);
    }

    public static void replaceBiomes(
            ServerPlayer nmsPlayer,
            String keyMappedBiome,
            Function<Identifier, Identifier> identifierModifier) {
        ServerLevel nmsWorld = nmsPlayer.level();

        int viewDistance = nmsWorld.getServer().getPlayerList().getViewDistance();
        ChunkPos center = nmsPlayer.chunkPosition();
        List<ClientboundChunksBiomesPacket.ChunkBiomeData> biomeDataList = new ArrayList<>();

        for (int cx = center.x() - viewDistance; cx <= center.x() + viewDistance; cx++) {
            for (int cz = center.z() - viewDistance; cz <= center.z() + viewDistance; cz++) {
                LevelChunk chunk = nmsWorld.getChunkIfLoaded(cx, cz);
                if (chunk == null) continue;

                LevelChunk fakeChunk = getFakeChunkWithMapping(chunk, keyMappedBiome, identifierModifier);
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

        Strategy<Holder<Biome>> idMap = Strategy.createForBiomes(original.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).asHolderIdMap());

        LevelChunkSection[] originalSections = original.getSections();
        LevelChunkSection[] fakeSections = fakeChunk.getSections();

        for (int i = 0; i < originalSections.length; i++) {
            PalettedContainer<Holder<Biome>> container = new PalettedContainer<>(
                    biome,
                    idMap,
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

    private static final Map<String, Holder<Biome>> BIOME_CACHE = new HashMap<>();
    /**
     * Crée un faux chunk en se basant sur l'originel et en changeant juste le biome du chunk en fonction
     * d'un identifierModifier
     * @param original le chunk original
     * @param identifierModifier le modifier d'identifiant
     * @return le faux chunk avec biomes modifiés
     */
    private static LevelChunk getFakeChunkWithMapping(
            LevelChunk original,
            String keyMappedBiome,
            Function<Identifier, Identifier> identifierModifier) {
        LevelChunk fake = new LevelChunk(original.getLevel(), original.getPos());

        Registry<Biome> registry = original.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        Strategy<Holder<Biome>> idMap = Strategy.createForBiomes(original.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).asHolderIdMap());
        LevelChunkSection[] originalSections = original.getSections();
        LevelChunkSection[] fakeSections = fake.getSections();

        for (int i = 0; i < originalSections.length; i++) {
            LevelChunkSection originalSection = originalSections[i];
            PalettedContainer<Holder<Biome>> originalBiomes = null;

            try {
                originalBiomes =
                        (PalettedContainer<Holder<Biome>>) SECTION_BIOMES.get(originalSection);
            } catch (IllegalAccessException e) {
                OMCLogger.error("Erreur d'acces à l'attribut biomes d'un levelChunkSetcion");
            }

            if (originalBiomes == null) continue;

            // ** Si le chunk contient qu'un biome
            if (originalBiomes.data.palette().getSize() == 1) {
                Holder<Biome> single = originalBiomes.data.palette().valueFor(0);
                Holder<Biome> mapped = getMapped(single, registry, keyMappedBiome, identifierModifier);

                PalettedContainer<Holder<Biome>> fakeBiomes1 = new PalettedContainer<>(mapped, idMap, null);
                try {
                    SECTION_BIOMES.set(fakeSections[i], fakeBiomes1);
                } catch (IllegalAccessException e) {
                    OMCLogger.error("Erreur d'acces à l'attribut biomes d'un levelChunkSetcion");
                }
                continue;
            }

            PalettedContainer<Holder<Biome>> fakeBiomes = new PalettedContainer<>(
                    originalSection.getNoiseBiome(0,0,0),
                    idMap,
                    null
            );

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    for (int z = 0; z < 4; z++) {
                        Holder<Biome> originalBiome = originalSection.getNoiseBiome(x, y, z);
                        Holder<Biome> mapped = getMapped(originalBiome, registry, keyMappedBiome, identifierModifier);

                        fakeBiomes.set(x, y, z, mapped);
                    }
                }
            }

            try {
                SECTION_BIOMES.set(fakeSections[i], fakeBiomes);
            } catch (IllegalAccessException e) {
                OMCLogger.error("Erreur d'acces à l'attribut biomes d'un levelChunkSetcion");
            }
        }

        return fake;
    }

    private static Holder<Biome> getMapped(
            Holder<Biome> original,
            Registry<Biome> registry,
            String keyMappedBiome,
            Function<Identifier, Identifier> identifierModifier) {
        Identifier originalId = original.unwrapKey().orElseThrow().identifier();
        String cacheKey = keyMappedBiome + "/" + originalId;

        return BIOME_CACHE.computeIfAbsent(cacheKey, k -> {
            Identifier mappedId = identifierModifier.apply(originalId);
            return registry.get(mappedId).orElseThrow();
        });
    }
}
