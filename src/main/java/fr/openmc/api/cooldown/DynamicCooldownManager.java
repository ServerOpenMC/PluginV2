package fr.openmc.api.cooldown;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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
        private final String uuid;
        private final String group;
        private final BukkitTask scheduledTask;

        /**
         * @param duration Cooldown duration in ms
         */
        public Cooldown(String uuid, String group, long duration, long lastUse) {
            this.duration = duration;
            this.lastUse = lastUse;
            this.uuid = uuid;
            this.group = group;

            Bukkit.getPluginManager().callEvent(new CooldownStartEvent(this.uuid, this.group));

            long delayTicks = duration / 50; //ticks
            this.scheduledTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new CooldownEndEvent(this.uuid, this.group));
                DynamicCooldownManager.clear(uuid, group);
            }, delayTicks);
        }

        public void cancelTask() {
            if (scheduledTask != null) scheduledTask.cancel();
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
    private static final HashMap<String, HashMap<String, Cooldown>> cooldowns = new HashMap<>();

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS cooldowns (uuid VARCHAR(36) PRIMARY KEY, `group` VARCHAR(36), cooldown_time BIGINT, last_used BIGINT);").executeUpdate();

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                Bukkit.getLogger().info("===== cooldowns Debug =====");
//
//                Bukkit.getLogger().info("cooldowns:");
//                System.out.println(cooldowns);
//                for (Map.Entry<String, HashMap<String, Cooldown>> entry1 : cooldowns.entrySet()) {
//                    for (Map.Entry<String, Cooldown> entry2 : entry1.getValue().entrySet()) {
//                        Bukkit.getLogger().info(entry1.getKey() + " -> group " + entry2.getKey() + " -> cooldown time " + entry2.getValue().duration + " lastUse " + entry2.getValue().lastUse);
//                    }
//                }
//
//
//                Bukkit.getLogger().info("================================");
//            }
//        }.runTaskTimer(OMCPlugin.getInstance(), 0, 600L); // 600 ticks = 30 secondes
    }

    public static void loadCooldowns() {
        String sql = "SELECT uuid, `group`, cooldown_time, last_used FROM cooldowns";

        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            ResultSet result = states.executeQuery();

            while (result.next()) {
                String uuid = result.getString("uuid");
                String group = result.getString("group");
                long lastUsed = result.getLong("last_used");
                long duration = result.getLong("cooldown_time");


                Cooldown cooldown = new Cooldown(uuid, group, duration, lastUsed);

                if (cooldown.isReady()) continue;

                HashMap<String, Cooldown> groupCooldowns = cooldowns.getOrDefault(uuid, new HashMap<>());

                groupCooldowns.put(group, cooldown);

                cooldowns.put(uuid, groupCooldowns);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des cooldowns depuis la base de données", e);
        }
    }

    public static void saveCooldowns() {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM cooldowns").executeUpdate();

            try (PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO cooldowns (`uuid`, `group`, `cooldown_time`, `last_used`) " +
                            "VALUES (?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "`cooldown_time` = VALUES(`cooldown_time`), `last_used` = VALUES(`last_used`)"
            )) {
                OMCPlugin.getInstance().getLogger().info("Sauvegarde des cooldowns actifs...");

                cooldowns.forEach((uuid, groupCooldowns) -> {
                    groupCooldowns.forEach((group, cooldown) -> {
                        if (cooldown.isReady()) return;
                        try {
                            statement.setString(1, uuid);
                            statement.setString(2, group);
                            statement.setLong(3, cooldown.duration);
                            statement.setLong(4, cooldown.lastUse);
                            statement.addBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });

                statement.executeBatch();

                OMCPlugin.getInstance().getLogger().info("Sauvegarde des cooldowns réussie.");
            }

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
    public static boolean isReady(String uuid, String group) {
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
    public static void use(String uuid, String group, long duration) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(group, new Cooldown(uuid, group, duration, System.currentTimeMillis()));
    }

    /**
     * Get remaining cooldown time
     * @param uuid Entity UUID
     * @param group Cooldown group
     * @return remaining time in milliseconds, 0 if no cooldown
     */
    public static long getRemaining(String uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null) return 0;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null ? 0 : cooldown.getRemaining();
    }

    /**
     * Réduit la durée restante d'un cooldown en cours.
     *
     * @param uuid            UUID de l'entité
     * @param group           Nom du groupe de cooldown
     * @param reductionMillis Réduction en millisecondes
     */
    public static void reduceCooldown(Player player, String uuid, String group, long reductionMillis) {
        var userCooldowns = cooldowns.get(uuid);

        if (userCooldowns == null) {
            return;
        }

        Cooldown cooldown = userCooldowns.get(group);
        if (cooldown == null) {
            return;
        }

        if (cooldown.isReady()) {
            return;
        }

        long remaining = cooldown.getRemaining();
        long newRemaining = Math.max(0, remaining - reductionMillis);

        cooldown.cancelTask();

        if (newRemaining == 0) {
            userCooldowns.remove(group);
            Bukkit.getPluginManager().callEvent(new CooldownEndEvent(uuid, group));
            if (userCooldowns.isEmpty()) cooldowns.remove(uuid);
            player.closeInventory();
            return;
        }

        long newLastUse = System.currentTimeMillis() - (cooldown.duration - newRemaining);
        Cooldown newCooldown = new Cooldown(uuid, group, cooldown.duration, newLastUse);
        userCooldowns.put(group, newCooldown);
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
    public static void clear(String uuid) {
        var userCooldowns = cooldowns.remove(uuid);
        if (userCooldowns != null) {
            userCooldowns.values().forEach(Cooldown::cancelTask);
        }
    }

    /**
     * Removes a specific cooldown group for an entity
     * @param uuid Entity UUID
     * @param group Cooldown group
     */
    public static void clear(String uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns != null) {
            Cooldown removed = userCooldowns.remove(group);
            if (removed != null) removed.cancelTask();
            if (userCooldowns.isEmpty()) cooldowns.remove(uuid);
        }
    }
}