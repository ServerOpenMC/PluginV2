package fr.openmc.core.utils.cache;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CachePlaytime {

    private static final TtlCache<UUID, Long> cache = new TtlCache<>(5, TimeUnit.MINUTES);

    public static long getPlaytime(OfflinePlayer player) {
        return cache.getOrCompute(
                player.getUniqueId(),
                _ -> (long) player.getStatistic(Statistic.PLAY_ONE_MINUTE)
        );
    }

    public static void refresh(OfflinePlayer player) {
        cache.put(player.getUniqueId(), (long) player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    public static void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }
}
