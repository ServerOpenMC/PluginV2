package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.utils.cache.TtlCache;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheDeathCount {

    private static final TtlCache<UUID, Long> cache = new TtlCache<>(5, TimeUnit.MINUTES);

    public static long getDeathCount(OfflinePlayer player) {
        return cache.getOrCompute(
                player.getUniqueId(),
                _ -> (long) player.getStatistic(Statistic.DEATHS)
        );
    }

    public static void refresh(OfflinePlayer player) {
        cache.put(player.getUniqueId(), (long) player.getStatistic(Statistic.DEATHS));
    }

    public static void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }
}
