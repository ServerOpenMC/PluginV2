package fr.openmc.api.cooldown;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

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
        private String id;
        @DatabaseField(canBeNull = false)
        private String group;
        @DatabaseField(canBeNull = false)
        private long duration;
        @DatabaseField(canBeNull = false)
        private long lastUse;
        private final BukkitTask scheduledTask;

        Cooldown() {
            // required for ORMLite
        }

        /**
         * @param duration Cooldown duration in ms
         */
        public Cooldown(String id, String group, long duration, long lastUse) {
            this.duration = duration;
            this.lastUse = lastUse;
            this.id = id;
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

    public DynamicCooldownManager() {
        loadCooldowns();
    }

    // Map structure: UUID -> (Group -> Cooldown)
    private static final HashMap<String, HashMap<String, Cooldown>> cooldowns = new HashMap<>();

    private static Dao<Cooldown, String> cooldownDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Cooldown.class);
        cooldownDao = DaoManager.createDao(connectionSource, Cooldown.class);
    }

    public static void loadCooldowns() {
        try {
            List<Cooldown> dbCooldowns = cooldownDao.queryForAll();

            for (Cooldown cooldown : dbCooldowns) {
                if (cooldown.isReady()) continue;
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
                if (cooldown.isReady()) return;
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
    public static boolean isReady(String uuid, String group) {
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
    public static void use(String uuid, String group, long duration) {
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
     *
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
     *
     * @param uuid  Entity UUID
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
