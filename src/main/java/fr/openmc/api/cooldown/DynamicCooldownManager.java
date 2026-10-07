package fr.openmc.api.cooldown;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.debug.DebugCooldownCommand;
import fr.openmc.core.commands.utils.CooldownCommand;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.*;

/**
 * Main class for managing cooldowns
 */
public class DynamicCooldownManager extends Feature implements HasDatabase, HasCommands {
    @Override
    public void onEnable() {
        loadCooldowns();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new DebugCooldownCommand(),
                new CooldownCommand()
        );
    }


    @Override
    public void onDisable() {
        this.saveCooldowns();
    }

    // Map structure: UUID -> (Group -> Cooldown)
    private final Map<UUID, Map<String, Cooldown>> cooldowns = new HashMap<>();

    private Dao<Cooldown, String> cooldownDao;

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Cooldown.class);
        cooldownDao = DaoManager.createDao(connectionSource, Cooldown.class);
    }

    public void loadCooldowns() {
        try {
            List<Cooldown> dbCooldowns = cooldownDao.queryForAll();

            for (Cooldown cooldown : dbCooldowns) {
                if (cooldown.isReady()) {
                    Bukkit.getPluginManager().callEvent(new CooldownEndEvent(cooldown.getUniqueId(), cooldown.getGroup()));
                    cooldownDao.delete(cooldown);
                    continue;
                }

                cooldowns.computeIfAbsent(cooldown.getUniqueId(), k -> new HashMap<>())
                        .put(cooldown.getGroup(), new Cooldown(cooldown.getUniqueId(), cooldown.getGroup(), cooldown.getDuration(), cooldown.getLastUse()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des cooldowns depuis la base de données", e);
        }
    }

    public void saveCooldowns() {
        OMCLogger.info("Saving cooldowns...");

        cooldowns.forEach((uuid, groupCooldowns) -> {
            groupCooldowns.forEach((group, cooldown) -> {
                if (!cooldown.isReady()) {
                    try {
                        cooldownDao.createOrUpdate(cooldown);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        cooldownDao.delete(cooldown);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });

        OMCLogger.info("Cooldowns saved successfully.");
    }

    /**
     * @param uuid Entity UUID to check
     * @return Map of cooldowns for the entity, or null if no cooldowns
     */
    public Map<String, Cooldown> getCooldowns(UUID uuid) {
        return cooldowns.get(uuid);
    }

    /**
     * @param uuid  Entity UUID to check
     * @param group Cooldown group
     * @return true if an entity can perform action
     */
    public boolean isReady(UUID uuid, String group) {
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
    public void use(UUID uuid, String group, long duration) {
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
    public long getRemaining(UUID uuid, String group) {
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
    public void reduceCooldown(Player player, UUID uuid, String group, long reductionMillis) {
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

        long newLastUse = System.currentTimeMillis() - (cooldown.getDuration() - newRemaining);
        Cooldown newCooldown = new Cooldown(uuid, group, cooldown.getDuration(), newLastUse);
        userCooldowns.put(group, newCooldown);
    }

    /**
     * Removes all expired cooldowns
     */
    public void cleanup() {
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
    public void clear(String group) {
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
    public void clear(UUID uuid, String group, boolean callEvent) {
        var userCooldowns = cooldowns.get(uuid);

        if (userCooldowns != null) {
            if (callEvent) Bukkit.getPluginManager().callEvent(new CooldownEndEvent(uuid, group));

            Cooldown removed = userCooldowns.remove(group);
            if (removed != null) removed.cancelTask();
            if (userCooldowns.isEmpty()) cooldowns.remove(uuid);
        }
    }
}
