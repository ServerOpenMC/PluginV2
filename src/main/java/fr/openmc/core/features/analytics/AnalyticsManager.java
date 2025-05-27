package fr.openmc.core.features.analytics;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.models.Statistic;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class AnalyticsManager {
    static Dao<Statistic, String> statsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Statistic.class);
        statsDao = DaoManager.createDao(connectionSource, Statistic.class);
    }

    public static boolean isEnabled() {
        return OMCPlugin.getConfigs().getBoolean("features.analytics", false);
    }

    /**
     * Return the stats for a player
     * 
     * @param scope        The scope of the statistics
     * @param player       Player
     * @param defaultValue The value that will get returned if analytics is disabled
     *                     or didn't work
     * @return The stats of the player, if unavailable, it will return defaultValue
     */
    static int getStatistic(String scope, UUID player, int defaultValue) {
        if (!isEnabled())
            return defaultValue;

        try {
            QueryBuilder<Statistic, String> query = statsDao.queryBuilder();
            query.where().eq("player", player).or().eq("scope", scope);
            List<Statistic> stats = statsDao.query(query.prepare());
            if (stats.size() == 0)
                return 0;

            return stats.get(0).getValue();
        } catch (SQLException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * Increment a player's stats
     * 
     * @param scope  The scope of the statistics
     * @param player Player
     * @param value  The amount to increment the statistic
     *
     */
    static void incrementStatistic(String scope, UUID player, int value) {
        if (!isEnabled())
            return;

        assert value > 0;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                QueryBuilder<Statistic, String> query = statsDao.queryBuilder();
                query.where().eq("player", player).or().eq("scope", scope);
                List<Statistic> stats = statsDao.query(query.prepare());
                if (stats.size() != 1)
                    return;
                statsDao.delete(stats);

                Statistic statistic = new Statistic(player, scope, stats.get(0).getValue() + value);
                statsDao.create(statistic);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
