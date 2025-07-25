package fr.openmc.core.features.analytics;

import java.util.UUID;

public enum Stats {
    SESSION("session"),
    TOTAL_TRANSACTIONS("total_transactions"),
    ;

    private String scope;

    Stats(String scope) {
        this.scope = scope;
    }

    /**
     * Return the stats for a player
     * 
     * @param playerUUID       Player
     * @param defaultValue The value if the analytics manager is disabled
     * @return The stats of the player, if unavailable, it will return `0`
     */
    public int get(UUID playerUUID, int defaultValue) {
        return AnalyticsManager.getStatistic(this.scope, playerUUID, defaultValue);
    }

    /**
     * Return the stats for a player
     * 
     * @param playerUUID Player
     * @return The stats of the player, if unavailable, it will return `0`
     */
    public int get(UUID playerUUID) {
        return get(playerUUID, 0);
    }

    /**
     * Increment a stat by one for a player
     * 
     * @param playerUUID Player
     * @param value  the amount to increment the statistic
     */
    public void increment(UUID playerUUID, int value) {
        AnalyticsManager.incrementStatistic(this.scope, playerUUID, value);
    }

    /**
     * Increment a stat by one for a player
     * 
     * @param playerUUID The player to increment the stat for
     */
    public void increment(UUID playerUUID) {
        increment(playerUUID, 1);
    }
}
