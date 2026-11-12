package fr.openmc.core.utils.cache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheQuizCount {
    private static final TtlCache<UUID, Integer> cache = new TtlCache<>(5, TimeUnit.MINUTES);

    public static int getCorrectAnswers(UUID uuid) {
        return cache.getOrCompute(uuid, _ -> 0);
    }

    public static void addCorrectAnswer(UUID uuid) {
        int current = getCorrectAnswers(uuid);
        cache.put(uuid, current + 1);
    }

    public static void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }
}

