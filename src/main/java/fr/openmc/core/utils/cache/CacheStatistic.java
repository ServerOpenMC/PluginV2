package fr.openmc.core.utils.cache;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheStatistic {

    private static final Map<Statistic, TtlCache<UUID, Integer>> cacheStats = new HashMap<>();

    private static TtlCache<UUID, Integer> cacheFor(Statistic statistic) {
        return cacheStats.computeIfAbsent(statistic, _ -> new TtlCache<>(5, TimeUnit.MINUTES));
    }

    public static int getStatistic(OfflinePlayer player, Statistic statistic) {
        return cacheFor(statistic).getOrCompute(
                player.getUniqueId(),
                _ -> player.getStatistic(statistic)
        );
    }

    public static void refresh(OfflinePlayer player, Statistic statistic) {
        cacheFor(statistic).put(player.getUniqueId(), player.getStatistic(statistic));
    }

    public static void invalidate(UUID uuid, Statistic statistic) {
        TtlCache<UUID, Integer> cache = cacheStats.get(statistic);
        if (cache != null) cache.invalidate(uuid);
    }
}
