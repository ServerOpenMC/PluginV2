package fr.openmc.core.features.analytics;

import java.util.UUID;

public enum Stats {
    SESSION("session"),
    TOTAL_TRANSACTIONS("total_transactions"),
    ;

    private final String scope;

    Stats(String scope) {
        this.scope = scope;
    }

    /**
     * Return the stats for a player
     * 
     * @param player       Player
     * @param defaultValue The value if analytics manager is disabled
     * @return The stats of the player, if unavailable, it will return `0`
     */
    public int get(UUID player, int defaultValue) {
        return AnalyticsManager.getStatistic(this.scope, player, defaultValue);
    }

    /**
     * Return the stats for a player
     * 
     * @param uuid Player
     * @return The stats of the player, if unavailable, it will return `0`
     */
    public int get(UUID uuid) {
        return get(uuid, 0);
    }

    /**
     * Increment a stats by one for a player
     * 
     * @param player Player
     * @param value  the amount to increment the statistic
     */
    public void increment(UUID player, int value) {
        AnalyticsManager.incrementStatistic(this.scope, player, value);
    }

    /**
     * Increment a stats by one for a player
     * 
     * @param player Player
     */
    public void increment(UUID player) {
        increment(player, 1);
    }
}
