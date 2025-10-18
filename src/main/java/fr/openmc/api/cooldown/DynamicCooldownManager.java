package fr.openmc.api.cooldown;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        private UUID uniqueId;
        @DatabaseField(canBeNull = false)
        private String group;
        @DatabaseField(canBeNull = false)
        private long duration;
        @DatabaseField(canBeNull = false)
        private long lastUse;
        private BukkitTask scheduledTask;

        Cooldown() {
            // required for ORMLite
        }

        /**
         * @param duration Cooldown duration in ms
         */
        public Cooldown(UUID cooldownUUID, String group, long duration, long lastUse) {
            this.duration = duration;
            this.lastUse = lastUse;
            this.uniqueId = cooldownUUID;
            this.group = group;

            Bukkit.getPluginManager().callEvent(new CooldownStartEvent(this.uniqueId, this.group));

            long delayTicks = getRemaining() / 50; //ticks

            this.scheduledTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new CooldownEndEvent(this.uniqueId, this.group));
                DynamicCooldownManager.clear(this.uniqueId, this.group, false);
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

    public static void init() {
        loadCooldowns();
    }

    // Map structure: UUID -> (Group -> Cooldown)
    private static final Map<UUID, Map<String, Cooldown>> cooldowns = new HashMap<>();

    private static Dao<Cooldown, String> cooldownDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Cooldown.class);
        cooldownDao = DaoManager.createDao(connectionSource, Cooldown.class);
    }

    public static void loadCooldowns() {
        try {
            List<Cooldown> dbCooldowns = cooldownDao.queryForAll();

            for (Cooldown cooldown : dbCooldowns) {
                if (cooldown.isReady()) {
                    Bukkit.getPluginManager().callEvent(new CooldownEndEvent(cooldown.uniqueId, cooldown.group));
                    cooldownDao.delete(cooldown);
                    continue;
                }

                cooldowns.computeIfAbsent(cooldown.uniqueId, k -> new HashMap<>())
                        .put(cooldown.group, new Cooldown(cooldown.uniqueId, cooldown.group, cooldown.duration, cooldown.lastUse));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des cooldowns depuis la base de données", e);
        }
    }

    public static void saveCooldowns() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Saving cooldowns...");

        cooldowns.forEach((uuid, groupCooldowns) -> {
            groupCooldowns.forEach((group, cooldown) -> {
                if (!cooldown.isReady()) {
                    try {
                        cooldownDao.createOrUpdate(cooldown);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });

        OMCPlugin.getInstance().getSLF4JLogger().info("Cooldowns saved successfully.");
    }

    /**
     * @param uuid Entity UUID to check
     * @return Map of cooldowns for the entity, or null if no cooldowns
     */
    public static Map<String, Cooldown> getCooldowns(UUID uuid) {
        return cooldowns.get(uuid);
    }

    /**
     * @param uuid  Entity UUID to check
     * @param group Cooldown group
     * @return true if an entity can perform action
     */
    public static boolean isReady(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null)
            return true;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null || cooldown.isReady();
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
     * Get remaining cooldown time
     *
     * @param uuid  Entity UUID
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
     * Réduit la durée restante d'un cooldown en cours.
     *
     * @param uuid            UUID de l'entité
     * @param group           Nom du groupe de cooldown
     * @param reductionMillis Réduction en millisecondes
     */
    public static void reduceCooldown(Player player, UUID uuid, String group, long reductionMillis) {
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
     * Removes all cooldowns for group
     *
     * @param group Cooldown group
     */
    public static void clear(String group) {
        cooldowns.forEach((uuid, userCooldowns) -> {
            Cooldown removed = userCooldowns.remove(group);
            if (removed != null) removed.cancelTask();
        });
        cooldowns.entrySet().removeIf(entry -> entry.getValue().isEmpty()); // A test
    }

    /**
     * Removes a specific cooldown group for an entity
     *
     * @param uuid  Entity UUID
     * @param group Cooldown group
     */
    public static void clear(UUID uuid, String group, boolean callEvent) {
        var userCooldowns = cooldowns.get(uuid);

        if (userCooldowns != null) {
            if (callEvent) Bukkit.getPluginManager().callEvent(new CooldownEndEvent(uuid, group));

            Cooldown removed = userCooldowns.remove(group);
            if (removed != null) removed.cancelTask();
            if (userCooldowns.isEmpty()) cooldowns.remove(uuid);
        }
    }
}
