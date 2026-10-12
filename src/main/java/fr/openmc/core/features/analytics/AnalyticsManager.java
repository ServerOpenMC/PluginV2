package fr.openmc.core.features.analytics;

import com.j256.ormlite.stmt.QueryBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.models.Statistic;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AnalyticsManager extends Feature implements HasDatabase {
    private final DBTable<Statistic, String> statsDao = DBTable.of(Statistic.class);

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(statsDao);
    }

    public boolean isEnabled() {
        return OMCPlugin.getConfigs().getBoolean("features.analytics", false);
    }

    /**
     * Return the stats for a player
     * 
     * @param scope        The scope of the statistics
     * @param playerUUID       playerUUID
     * @param defaultValue The value that will get returned if analytics is disabled
     *                     or didn't work
     * @return The stats of the player, if unavailable, it will return defaultValue
     */
    public int getStatistic(String scope, UUID playerUUID, int defaultValue) {
        if (!isEnabled())
            return defaultValue;

        try {
            QueryBuilder<Statistic, String> query = statsDao.queryBuilder();
            query.where().eq("player", playerUUID).or().eq("scope", scope);
            List<Statistic> stats = statsDao.query(query.prepare());
            if (stats.isEmpty())
                return 0;

            return stats.getFirst().getValue();
        } catch (SQLException e) {
            OMCLogger.error("Failed to get Statistics of {}", playerUUID, e);
            return defaultValue;
        }
    }

    /**
     * Increment a player's stats
     * 
     * @param scope  The scope of the statistics
     * @param playerUUID PlayerUUID
     * @param value  The amount to increment the statistic
     *
     */
    public void incrementStatistic(String scope, UUID playerUUID, int value) {
        if (!isEnabled())
            return;

        assert value > 0;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                QueryBuilder<Statistic, String> query = statsDao.queryBuilder();
                query.where().eq("player", playerUUID).or().eq("scope", scope);
                List<Statistic> stats = statsDao.query(query.prepare());
                if (stats.size() != 1)
                    return;
                statsDao.delete(stats);

                Statistic statistic = new Statistic(playerUUID, scope, stats.getFirst().getValue() + value);
                statsDao.create(statistic);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
