package fr.openmc.core.utils.cooldown;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Main class for managing cooldowns
 */
public class DynamicCooldownManager {
    /**
     * Represents a single cooldown with duration and last use time
     */
    public static class Cooldown {
        private final long duration;
        private long lastUse;

        /**
         * @param duration Cooldown duration in ms
         */
        public Cooldown(long duration, long lastUse) {
            this.duration = duration;
            this.lastUse = lastUse;
        }

        /**
         * @return true if cooldown has expired
         */
        public boolean isReady() {
            return System.currentTimeMillis() - lastUse > duration;
        }

        /**
         * @return remaining time in milliseconds
         */
        public long getRemaining() {
            return Math.max(0, duration - (System.currentTimeMillis() - lastUse));
        }
    }

    // Map structure: UUID -> (Group -> Cooldown)
    private static final HashMap<UUID, HashMap<String, Cooldown>> cooldowns = new HashMap<>();

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS cooldowns (uuid VARCHAR(36) PRIMARY KEY, group VARCHAR(36), cooldown_time BIGINT, last_used BIGINT);").executeUpdate();
    }

    public static void loadCooldowns() {
        String sql = "SELECT uuid, `group`, cooldown_time, last_used FROM cooldowns";

        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            ResultSet result = states.executeQuery();

            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String group = result.getString("group");
                long lastUsed = result.getLong("last_used");
                long duration = result.getLong("cooldown_time");

                HashMap<String, Cooldown> groupCooldowns = cooldowns.getOrDefault(uuid, new HashMap<>());

                groupCooldowns.put(group, new Cooldown(duration, lastUsed));

                cooldowns.put(uuid, groupCooldowns);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des cooldowns depuis la base de données", e);
        }
    }

    public static void saveCooldowns() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO cooldowns (uuid, group, cooldown_time, last_used) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "uuid = VALUES(uuid), group = VALUES(group), cooldown_time = VALUES(cooldown_time), last_used = VALUES(last_used)"
        )) {
            OMCPlugin.getInstance().getLogger().info("Sauvegarde des cooldowns...");
            cooldowns.forEach((uuid, groupCooldowns) -> {
                groupCooldowns.forEach((group, cooldown) -> {
                    try {
                        statement.setString(1, uuid.toString());
                        statement.setString(2, group);
                        statement.setLong(3, cooldown.duration);
                        statement.setLong(3, cooldown.lastUse);

                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            statement.executeBatch();

            OMCPlugin.getInstance().getLogger().info("Sauvegarde des cooldowns réussi.");
        } catch (SQLException e) {
            OMCPlugin.getInstance().getLogger().severe("Echec de la sauvegarde des cooldowns.");
            e.printStackTrace();
        }
    }


    /**
     * @param uuid Entity UUID to check
     * @param group Cooldown group
     * @return true if entity can perform action
     */
    public static boolean isReady(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null) return true;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null || cooldown.isReady();
    }

    /**
     * Puts entity on cooldown
     * @param uuid Entity UUID
     * @param group Cooldown group
     * @param duration Cooldown duration in ms
     */
    public static void use(UUID uuid, String group, long duration) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(group, new Cooldown(duration, System.currentTimeMillis()));
    }

    /**
     * Get remaining cooldown time
     * @param uuid Entity UUID
     * @param group Cooldown group
     * @return remaining time in milliseconds, 0 if no cooldown
     */
    public static long getRemaining(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null) return 0;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null ? 0 : cooldown.getRemaining();
    }

    /**
     * Removes all expired cooldowns
     */
    public static void cleanup() {
        cooldowns.entrySet().removeIf(entry -> {
            entry.getValue().entrySet().removeIf(groupEntry -> groupEntry.getValue().isReady());
            return entry.getValue().isEmpty();
        });
    }

    /**
     * Removes all cooldowns for a specific entity
     * @param uuid Entity UUID
     */
    public static void clear(UUID uuid) {
        cooldowns.remove(uuid);
    }

    /**
     * Removes a specific cooldown group for an entity
     * @param uuid Entity UUID
     * @param group Cooldown group
     */
    public static void clear(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns != null) {
            userCooldowns.remove(group);
            if (userCooldowns.isEmpty()) {
                cooldowns.remove(uuid);
            }
        }
    }
}