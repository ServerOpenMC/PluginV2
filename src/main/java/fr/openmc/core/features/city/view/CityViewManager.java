package fr.openmc.core.features.city.view;

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
    private static final Particle.DustOptions RED_DUST = new Particle.DustOptions(Color.RED, 3F);

    private static final Map<UUID, CityViewData> activeViewers = new ConcurrentHashMap<>();

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
                Component.text("Visualisation des claims des villes."),
                Prefix.CITY
        );
    }

    public static void stopView(@NotNull Player player) {
        CityViewData currentView = activeViewers.get(player.getUniqueId());
        if (currentView == null)
            return;

        currentView.task().cancel();
        activeViewers.remove(player.getUniqueId());
    }

    public static void updateAllViews() {
        activeViewers.keySet().forEach(CityViewManager::updateView);
    }

    private static void updateView(@NotNull UUID playerUUID) {
        CityViewData viewData = activeViewers.get(playerUUID);
        if (viewData == null)
            return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;

        Object2ObjectMap<ChunkPos, City> claimsToShow = collectClaimsInRadius(player);
        activeViewers.put(playerUUID, new CityViewData(viewData.task(), claimsToShow));
    }

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

    private static ScheduledTask createViewTask(@NotNull Player player, @NotNull City playerCity) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(OMCPlugin.getInstance(), task -> {
            CityViewData viewData = activeViewers.get(player.getUniqueId());
            if (viewData == null)
                return;

            viewData.claims().forEach((chunkPos, city) -> {
                showChunkBorders(player, chunkPos, city, playerCity.equals(city), player.getLocation().getBlockY());
            });
        }, 0L, VIEW_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private static void scheduleViewExpiration(@NotNull Player player) {
        Bukkit.getAsyncScheduler().runDelayed(OMCPlugin.getInstance(), task -> {
            CityViewData viewData = activeViewers.get(player.getUniqueId());
            if (viewData == null)
                return;

            viewData.task().cancel();
        }, VIEW_DURATION_SECONDS, TimeUnit.SECONDS);
    }

    private static void showChunkBorders(@NotNull Player player, @NotNull ChunkPos chunkPos, @NotNull City city, boolean isPlayerCity, int playerY) {
        List<Location> particleLocations = calculateParticleLocations(chunkPos, city, playerY);

        Particle particle = isPlayerCity ? Particle.CHERRY_LEAVES : Particle.DUST;
        Particle.DustOptions data = isPlayerCity ? null : RED_DUST;
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

    @NotNull
    private static List<Location> calculateParticleLocations(@NotNull ChunkPos chunkPos, @NotNull City city, int y) {
        List<Location> locations = new ArrayList<>();
        World world = chunkPos.getChunkInWorld().getWorld();
        int baseX = chunkPos.x();
        int baseZ = chunkPos.z();
        boolean[] borders = checkBorders(chunkPos, city);

        // borders[0]: bord haut (nord) → ligne z = baseZ
        if (borders[0]) {
            for (int x = 0; x <= CHUNK_SIZE; x++)
                locations.add(new Location(world, baseX + x, y, baseZ));
        }

        // borders[2]: bord bas (sud) → ligne z = baseZ + CHUNK_SIZE
        if (borders[2]) {
            for (int x = 0; x <= CHUNK_SIZE; x++)
                locations.add(new Location(world, baseX + x, y, baseZ + CHUNK_SIZE));
        }

        // borders[3]: bord gauche (ouest) → colonne x = baseX
        if (borders[3]) {
            for (int z = 0; z <= CHUNK_SIZE; z++)
                locations.add(new Location(world, baseX, y, baseZ + z));
        }

        // borders[1]: bord droit (est) → colonne x = baseX + CHUNK_SIZE
        if (borders[1]) {
            for (int z = 0; z <= CHUNK_SIZE; z++)
                locations.add(new Location(world, baseX + CHUNK_SIZE, y, baseZ + z));
        }

        return locations;
    }

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
