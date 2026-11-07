package fr.openmc.core.utils.cache;

import com.j256.ormlite.stmt.query.In;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheDeathCount {

    private static final TtlCache<UUID, Integer> cache = new TtlCache<>(5, TimeUnit.MINUTES);

    public static int getDeathCount(OfflinePlayer player) {
        return cache.getOrCompute(
                player.getUniqueId(),
                _ -> player.getStatistic(Statistic.DEATHS)
        );
    }

    public static void refresh(OfflinePlayer player) {
        cache.put(player.getUniqueId(), player.getStatistic(Statistic.DEATHS));
    }

    public static void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }
}
