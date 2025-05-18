package fr.openmc.api.cooldown;

import fr.openmc.core.OMCPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

/**
 * Main class for managing cooldowns
 */
public class DynamicCooldownManager {
    /**
     * Represents a single cooldown with duration and last use time
     */
    @DatabaseTable(tableName = "cooldowns")
    public static class Cooldown {
        @DatabaseField(id = true)
        private UUID id;
        @DatabaseField(canBeNull = false)
        private String group;
        @DatabaseField(canBeNull = false)
        private long duration;
        @DatabaseField(canBeNull = false)
        private long lastUse;

        Cooldown() {
            // required for ORMLite
        }

        /**
         * @param duration Cooldown duration in ms
         */
        public Cooldown(UUID id, String group, long duration, long lastUse) {
            this.id = id;
            this.group = group;
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

    private static Dao<Cooldown, UUID> cooldownDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Cooldown.class);
        cooldownDao = DaoManager.createDao(connectionSource, Cooldown.class);
        // new BukkitRunnable() {
        // @Override
        // public void run() {
        // Bukkit.getLogger().info("===== cooldowns Debug =====");
        //
        // Bukkit.getLogger().info("cooldowns:");
        // System.out.println(cooldowns);
        // for (Map.Entry<String, HashMap<String, Cooldown>> entry1 :
        // cooldowns.entrySet()) {
        // for (Map.Entry<String, Cooldown> entry2 : entry1.getValue().entrySet()) {
        // Bukkit.getLogger().info(entry1.getKey() + " -> group " + entry2.getKey() + "
        // -> cooldown time " + entry2.getValue().duration + " lastUse " +
        // entry2.getValue().lastUse);
        // }
        // }
        //
        //
        // Bukkit.getLogger().info("================================");
        // }
        // }.runTaskTimer(OMCPlugin.getInstance(), 0, 600L); // 600 ticks = 30 secondes
    }

    public static void loadCooldowns() {
        try {
            List<Cooldown> dbCooldowns = cooldownDao.queryForAll();

            for (Cooldown cooldown : dbCooldowns) {
                HashMap<String, Cooldown> groupCooldowns = cooldowns.getOrDefault(cooldown.id, new HashMap<>());
                groupCooldowns.put(cooldown.group, cooldown);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des cooldowns depuis la base de données", e);
        }
    }

    public static void saveCooldowns() {
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des cooldowns...");
        cooldowns.forEach((uuid, groupCooldowns) -> {
            groupCooldowns.forEach((group, cooldown) -> {
                try {
                    cooldownDao.createOrUpdate(cooldown);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        OMCPlugin.getInstance().getLogger().info("Sauvegarde des cooldowns réussie.");
    }

    /**
     * @param uuid  Entity UUID to check
     * @param group Cooldown group
     * @return true if entity can perform action
     */
    public static boolean isReady(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null)
            return true;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null || cooldown.isReady();
    }

    /**
     * @param uuid  Entity UUID to check
     * @param group Cooldown group
     * @return true if entity can perform action
     */
    public static boolean isReady(String uuid, String group) {
        return isReady(UUID.fromString(uuid), group);
    }

    /**
     * Puts entity on cooldown
     * 
     * @param uuid     Entity UUID
     * @param group    Cooldown group
     * @param duration Cooldown duration in ms
     */
    public static void use(UUID uuid, String group, long duration) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(group, new Cooldown(uuid, group, duration, System.currentTimeMillis()));
    }

    /**
     * Puts entity on cooldown
     * 
     * @param uuid     Entity UUID
     * @param group    Cooldown group
     * @param duration Cooldown duration in ms
     */
    public static void use(String uuid, String group, long duration) {
        use(UUID.fromString(uuid), group, duration);
    }

    /**
     * Get remaining cooldown time
     * 
     * @param uuid  Entity UUID
     * @param group Cooldown group
     * @return remaining time in milliseconds, 0 if no cooldown
     */
    public static long getRemaining(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null)
            return 0;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null ? 0 : cooldown.getRemaining();
    }

    /**
     * Get remaining cooldown time
     * 
     * @param uuid  Entity UUID
     * @param group Cooldown group
     * @return remaining time in milliseconds, 0 if no cooldown
     */
    public static long getRemaining(String uuid, String group) {
        return getRemaining(UUID.fromString(uuid), group);
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
     * 
     * @param uuid Entity UUID
     */
    public static void clear(UUID uuid) {
        cooldowns.remove(uuid);
    }

    /**
     * Removes all cooldowns for a specific entity
     * 
     * @param uuid Entity UUID
     */
    public static void clear(String uuid) {
        clear(UUID.fromString(uuid));
    }

    /**
     * Removes a specific cooldown group for an entity
     * 
     * @param uuid  Entity UUID
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

    /**
     * Removes a specific cooldown group for an entity
     * 
     * @param uuid  Entity UUID
     * @param group Cooldown group
     */
    public static void clear(String uuid, String group) {
        clear(UUID.fromString(uuid), group);
    }
}
