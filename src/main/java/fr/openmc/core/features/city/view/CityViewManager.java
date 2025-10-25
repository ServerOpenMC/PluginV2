package fr.openmc.core.features.city.view;

import fr.openmc.api.hooks.WorldGuardHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.ChunkPos;
import fr.openmc.core.utils.ParticleUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CityViewManager {
    private static final int VIEW_RADIUS_CHUNKS = 8;
    private static final long VIEW_DURATION_SECONDS = 30L;
    private static final long VIEW_INTERVAL_SECONDS = 1L;
    private static final int CHUNK_SIZE = 16;
    private static final int[][] ADJACENT_OFFSETS = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    private static final Map<UUID, CityViewData> activeViewers = new ConcurrentHashMap<>();

    /**
     * Démarre la visualisation des claims pour un joueur.
     *
     * @param player joueur qui active la visualisation
     */
    public static void startView(@NotNull Player player) {
        stopView(player);

        Object2ObjectMap<ChunkPos, City> claimsToShow = collectClaimsInRadius(player);

        if (claimsToShow.isEmpty()) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("Aucune ville n'a été trouvée dans les environs.", NamedTextColor.RED),
                    Prefix.CITY,
                    MessageType.ERROR,
                    false
            );
        }

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        ScheduledTask task = createViewTask(player, playerCity);
        activeViewers.put(player.getUniqueId(), new CityViewData(task, claimsToShow));
        scheduleViewExpiration(player);

        MessagesManager.sendMessage(
                player,
                Component.text("Visualisation des claims des villes en cours pendant 30 secondes"),
                Prefix.CITY
        );
    }

    /**
     * Arrête la visualisation pour un joueur.
     *
     * @param player joueur concerné
     */
    public static void stopView(@NotNull Player player) {
        CityViewData currentView = activeViewers.get(player.getUniqueId());
        if (currentView == null)
            return;

        currentView.task().cancel();
        activeViewers.remove(player.getUniqueId());
    }

    /**
     * Met à jour la visualisation de tous les joueurs actifs.
     */
    public static void updateAllViews() {
        activeViewers.keySet().forEach(CityViewManager::updateView);
    }

    /**
     * Met à jour la visualisation d’un joueur spécifique.
     *
     * @param playerUUID UUID du joueur
     */
    public static void updateView(@NotNull UUID playerUUID) {
        CityViewData viewData = activeViewers.get(playerUUID);
        if (viewData == null)
            return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;

        Object2ObjectMap<ChunkPos, City> claimsToShow = collectClaimsInRadius(player);
        activeViewers.put(playerUUID, new CityViewData(viewData.task(), claimsToShow));
    }

    /**
     * Récupère tous les claims de villes dans un rayon autour du joueur.
     *
     * @param player joueur concerné
     * @return map des positions de chunks et des villes correspondantes
     */
    @NotNull
    private static Object2ObjectMap<ChunkPos, City> collectClaimsInRadius(@NotNull Player player) {
        Object2ObjectMap<ChunkPos, City> claims = new Object2ObjectOpenHashMap<>();
        ChunkPos playerChunk = ChunkPos.fromChunk(player.getChunk());

        for (int x = -VIEW_RADIUS_CHUNKS; x <= VIEW_RADIUS_CHUNKS; x++) {
            for (int z = -VIEW_RADIUS_CHUNKS; z <= VIEW_RADIUS_CHUNKS; z++) {
                int chunkX = playerChunk.x() + x;
                int chunkZ = playerChunk.z() + z;

                ChunkPos claim = new ChunkPos(chunkX, chunkZ);
                City city = CityManager.getCityFromChunk(claim);
                if (city == null)
                    continue;

                claims.put(claim, city);
            }
        }

        return claims;
    }

    /**
     * Crée une tâche répétée qui affiche les particules des claims.
     *
     * @param player     joueur concerné
     * @param playerCity ville du joueur (peut être null)
     * @return tâche planifiée
     */
    private static ScheduledTask createViewTask(@NotNull Player player, @Nullable City playerCity) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(OMCPlugin.getInstance(), task -> {
            CityViewData viewData = activeViewers.get(player.getUniqueId());
            if (viewData == null)
                return;

            viewData.claims().forEach((chunkPos, city) -> {
                showChunkBorders(player, chunkPos, city, city.equals(playerCity), WorldGuardHook.doesChunkContainWGRegion(chunkPos.getChunkInWorld()), player.getLocation().getBlockY() + 1);
            });
        }, 0L, VIEW_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Programme l’arrêt automatique de la visualisation après un délai.
     *
     * @param player joueur concerné
     */
    private static void scheduleViewExpiration(@NotNull Player player) {
        Bukkit.getAsyncScheduler().runDelayed(OMCPlugin.getInstance(), task -> {
            CityViewData viewData = activeViewers.get(player.getUniqueId());
            if (viewData == null)
                return;

            viewData.task().cancel();
        }, VIEW_DURATION_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Affiche les particules représentant les bordures d’un chunk.
     *
     * @param player       joueur qui voit les particules
     * @param chunkPos     position du chunk
     * @param city         ville propriétaire du chunk
     * @param isPlayerCity true si c’est la ville du joueur
     * @param playerY      hauteur du joueur (pour placer les particules)
     */
    private static void showChunkBorders(@NotNull Player player, @NotNull ChunkPos chunkPos, @NotNull City city, boolean isPlayerCity, boolean isRegion, int playerY) {
        List<Location> particleLocations = calculateParticleLocations(chunkPos, city, playerY);

        Particle particle = isPlayerCity ? Particle.CHERRY_LEAVES : Particle.TINTED_LEAVES;
        Object data = isPlayerCity ? null : isRegion ? Color.ORANGE : Color.RED;
        particleLocations.forEach(location ->
                ParticleUtils.sendParticlePacket(
                        player,
                        location,
                        particle,
                        1,
                        0D, 0D, 0D,
                        0D,
                        data
                )
        );
    }

    /**
     * Calcule les positions des particules pour dessiner les bordures d’un chunk.
     *
     * @param chunkPos position du chunk
     * @param city     ville propriétaire
     * @param y        hauteur d’affichage
     * @return liste des positions de particules
     */
    @NotNull
    private static List<Location> calculateParticleLocations(@NotNull ChunkPos chunkPos,
                                                             @NotNull City city, int y) {
        List<Location> locations = new ArrayList<>();
        World world = chunkPos.getChunkInWorld().getWorld();
        int baseX = chunkPos.x() * 16;
        int baseZ = chunkPos.z() * 16;
        boolean[] borders = checkBorders(chunkPos, city);

        // Bord nord
        if (borders[0]) {
            for (int x = 0; x <= CHUNK_SIZE * 2; x++)
                locations.add(new Location(world, baseX + x / 2D, y, baseZ));
        }

        // Bord sud
        if (borders[2]) {
            for (int x = 0; x <= CHUNK_SIZE * 2; x++)
                locations.add(new Location(world, baseX + x / 2D, y, baseZ + CHUNK_SIZE));
        }

        // Bord ouest
        if (borders[3]) {
            for (int z = 0; z <= CHUNK_SIZE * 2; z++)
                locations.add(new Location(world, baseX, y, baseZ + z / 2D));
        }

        // Bord est
        if (borders[1]) {
            for (int z = 0; z <= CHUNK_SIZE * 2; z++)
                locations.add(new Location(world, baseX + CHUNK_SIZE, y, baseZ + z / 2D));
        }

        return locations;
    }

    /**
     * Vérifie quelles bordures d’un chunk doivent être affichées
     * (si le chunk adjacent n’appartient pas à la même ville).
     *
     * @param chunkPos position du chunk
     * @param city     ville propriétaire
     * @return tableau de 4 booléens (N, E, S, O)
     */
    private static boolean @NotNull [] checkBorders(@NotNull ChunkPos chunkPos, @NotNull City city) {
        boolean[] borders = new boolean[4];
        for (int i = 0; i < 4; i++) {
            ChunkPos adjacentClaim = new ChunkPos(
                    chunkPos.x() + ADJACENT_OFFSETS[i][0],
                    chunkPos.z() + ADJACENT_OFFSETS[i][1]
            );

            borders[i] = !city.hasChunk(adjacentClaim.x(), adjacentClaim.z());
        }

        return borders;
    }
}