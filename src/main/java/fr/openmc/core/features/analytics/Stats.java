package fr.openmc.core.features.analytics;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public enum Stats {
    SESSION,
    ;

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
