package fr.openmc.core.features.utils.analytics;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public enum Stats {
    SESSION,
    ;

    /**
     * Return the stats for a player
     * @param uuid Player
     * @param defaultValue The value that will get returned if analytics is disabled or didn't work
     * @return The stats of the player, if unavailable, it will return defaultValue
     */
    public int get(UUID uuid, int defaultValue) {
        if (!AnalyticsManager.isEnabled()) return defaultValue;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT value FROM stats WHERE player = ? AND scope = ? LIMIT 1");
            statement.setString(1, uuid.toString());
            statement.setString(2, this.name());
            ResultSet resultSet = statement.executeQuery();

            return resultSet.getInt(0);
        } catch (SQLException err) {
            err.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * Return the stats for a player
     * @param uuid Player
     * @return The stats of the player, if unavailable, it will return `0`
     */
    public int get(UUID uuid) {
        return get(uuid, 0);
    }

    /**
     * Increment a stats for a player
     * @param uuid Player
     * @param amount Amount to incremented
     */
    public void increment(UUID uuid, int amount) {
        if (!AnalyticsManager.isEnabled()) return;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO stats VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value=value+?");
                statement.setString(1, uuid.toString());
                statement.setString(2, this.name());
                statement.setInt(3, amount);
                statement.setInt(4, amount);
                statement.executeUpdate();
            } catch (SQLException err) {
                err.printStackTrace();
            }
        });
    }

    /**
     * Increment a stats by one for a player
     * @param uuid Player
     */
    public void increment(UUID uuid) {
        increment(uuid, 1);
    }

    /**
     * Decrement a stats by one for a player
     * @param uuid Player
     */
    public void decrement(UUID uuid) {
        increment(uuid, -1);
    }

    /**
     * Decrement a stats by one for a player
     * @param uuid Player
     * @param amount Amount to be decremented
     */
    public void decrement(UUID uuid, int amount) {
        increment(uuid, amount*-1);
    }
}
