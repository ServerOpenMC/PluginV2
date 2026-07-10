package fr.openmc.core.features.combat;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatCooldownManager {

    private static final long COMBAT_COOLDOWN_MILLIS = Duration.ofSeconds(10).toMillis();
    private static final ConcurrentHashMap<UUID, Long> LAST_HIT_TIMES = new ConcurrentHashMap<>();

    private CombatCooldownManager() {
    }

    public static void recordHit(UUID playerId) {
        LAST_HIT_TIMES.put(playerId, System.currentTimeMillis());
    }

    public static long getRemainingSeconds(UUID playerId) {
        Long lastHitTime = LAST_HIT_TIMES.get(playerId);
        if (lastHitTime == null) {
            return 0;
        }

        long remainingMillis = lastHitTime + COMBAT_COOLDOWN_MILLIS - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            LAST_HIT_TIMES.remove(playerId, lastHitTime);
            return 0;
        }

        return Math.ceilDiv(remainingMillis, 1000L);
    }

    public static void clear(UUID playerId) {
        LAST_HIT_TIMES.remove(playerId);
    }
}
