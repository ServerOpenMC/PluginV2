package fr.openmc.core.features.city.sub.statistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.statistics.models.CityStatistics;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 * Gestionnaire des statistiques des villes.
 *
 * <p>Cette classe gère le chargement, la sauvegarde et la mise à jour asynchrone des statistiques associées à chaque ville.</p>
 */
public class CityStatisticsManager {

    /**
     * Map des statistiques par ville (clé : identifiant de la ville, valeur : ensemble de statistiques).
     */
    public static HashMap<UUID, Set<CityStatistics>> cityStatistics = new HashMap<>();

    private static Dao<CityStatistics, String> statisticsDao;

    /**
     * Constructeur qui initialise le chargement des statistiques.
     */
    public CityStatisticsManager() {
        loadCityStatistics();
    }

    /**
     * Initialise la base de données pour les statistiques des villes.
     *
     * @param connectionSource la source de connexion
     * @throws SQLException en cas d'erreur SQL
     */
    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityStatistics.class);
        statisticsDao = DaoManager.createDao(connectionSource, CityStatistics.class);
    }

    /**
     * Charge toutes les statistiques depuis la base de données.
     */
    public static void loadCityStatistics() {
        try {
            List<CityStatistics> statistics = statisticsDao.queryForAll();
            statistics.forEach(statistic -> cityStatistics.computeIfAbsent(statistic.getCityUUID(), k -> new HashSet<>()).add(statistic));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde toutes les statistiques dans la base de données.
     */
    public static void saveCityStatistics() {
        cityStatistics.forEach((city, statistics) -> statistics.forEach(stat -> {
            try {
                statisticsDao.createOrUpdate(stat);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * Retourne l'ensemble des statistiques pour une ville avec laquelle en crée un nouveau.
     *
     * @param cityUUID l'identifiant de la ville
     * @return l'ensemble des statistiques associées à la ville
     */
    public static Set<CityStatistics> getOrCreate(UUID cityUUID) {
        return cityStatistics.computeIfAbsent(cityUUID, k -> new HashSet<>());
    }

    /**
     * Retourne ou crée une statistique pour une ville et un scope donnés.
     *
     * @param cityUUID l'identifiant de la ville
     * @param scope    le scope de la statistique
     * @return la statistique correspondante
     */
    public static CityStatistics getOrCreateStat(UUID cityUUID, String scope) {
        Set<CityStatistics> stats = getOrCreate(cityUUID);
        for (CityStatistics stat : stats) {
            if (stat != null && scope.equals(stat.getScope())) {
                return stat;
            }
        }
        CityStatistics newStat = new CityStatistics(cityUUID, scope, 0);
        stats.add(newStat);
        return newStat;
    }

    /**
     * Met à jour la valeur d'une statistique pour une ville et la sauvegarde de manière asynchrone.
     *
     * @param cityUUID l'identifiant de la ville
     * @param scope    le scope de la statistique
     * @param value    la nouvelle valeur
     */
    public static void setStat(UUID cityUUID, String scope, Serializable value) {
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

    /**
     * Supprime toutes les statistiques d'une ville et les efface de la base de données.
     *
     * @param cityUUID l'identifiant de la ville
     */
    public static void removeStats(UUID cityUUID) {
        if (!cityStatistics.containsKey(cityUUID)) return;
        cityStatistics.remove(cityUUID);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                DeleteBuilder<CityStatistics, String> statisticsDelete = statisticsDao.deleteBuilder();
                statisticsDelete.where().eq("city_uuid", cityUUID);
                statisticsDao.delete(statisticsDelete.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Incrémente la valeur d'une statistique pour une ville d'un certain montant et sauvegarde la mise à jour de manière asynchrone.
     *
     * @param cityUUID l'identifiant de la ville
     * @param scope    le scope de la statistique
     * @param amount   le montant à ajouter
     */
    public static void increment(UUID cityUUID, String scope, long amount) {
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

    /**
     * Retourne la valeur d'une statistique pour une ville et un scope donnés.
     *
     * @param cityUUID l'identifiant de la ville
     * @param scope    le scope de la statistique
     * @return la valeur de la statistique
     */
    public static Object getStatValue(UUID cityUUID, String scope) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        return stat.getValue();
    }
}
