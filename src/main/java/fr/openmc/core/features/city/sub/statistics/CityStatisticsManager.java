package fr.openmc.core.features.city.sub.statistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.models.DBCityMember;
import fr.openmc.core.features.city.sub.statistics.models.CityStatistics;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CityStatisticsManager {

    public static HashMap<String, Set<CityStatistics>> cityStatistics = new HashMap<>(); // cityUUID -> CityStatistics
    private static Dao<CityStatistics, String> statisticsDao;

    public CityStatisticsManager() {
        loadCityStatistics();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityStatistics.class);
        statisticsDao = DaoManager.createDao(connectionSource, CityStatistics.class);
    }

    public static void loadCityStatistics() {
        try {
            List<CityStatistics> statistics = statisticsDao.queryForAll();

            statistics.forEach(statistic -> {
                cityStatistics.computeIfAbsent(statistic.getCityUUID(), k -> new HashSet<>()).add(statistic);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveCityStatistics() {
        cityStatistics.forEach(
                (city, statistics) -> {
                    statistics.forEach(stat -> {
                        try {
                            statisticsDao.createOrUpdate(stat);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                });
    }

    public static Set<CityStatistics> getOrCreate(String cityUUID) {
        return cityStatistics.computeIfAbsent(cityUUID, k -> new HashSet<>());
    }

    public static CityStatistics getOrCreateStat(String cityUUID, String scope) {
        Set<CityStatistics> stats = getOrCreate(cityUUID);
        for (CityStatistics stat : stats) {
            if (stat != null && scope.equals(stat.getScope())) return stat;
        }
        CityStatistics newStat = new CityStatistics(cityUUID, scope, 0);
        stats.add(newStat);
        return newStat;
    }

    public static void setStat(String cityUUID, String scope, Serializable value) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        stat.setValue(value);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                statisticsDao.createOrUpdate(stat);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void removeStats(String cityUUID) {
        if (!cityStatistics.containsKey(cityUUID)) return;
        cityStatistics.remove(cityUUID);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                DeleteBuilder<CityStatistics, String> statisticsDelete = statisticsDao.deleteBuilder();
                statisticsDelete.where().eq("cityUUID", cityUUID);
                statisticsDao.delete(statisticsDelete.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void increment(String cityUUID, String scope, long amount) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        long current = stat.asLong();
        stat.setValue(current + amount);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                statisticsDao.createOrUpdate(stat);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static Object getStatValue(String cityUUID, String scope) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        return stat.getValue();
    }
}
