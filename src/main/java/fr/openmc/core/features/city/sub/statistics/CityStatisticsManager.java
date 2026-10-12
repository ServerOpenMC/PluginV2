package fr.openmc.core.features.city.sub.statistics;

import com.j256.ormlite.stmt.DeleteBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.statistics.models.CityStatistics;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 * Gestionnaire des statistiques des villes.
 *
 * <p>Cette classe gère le chargement, la sauvegarde et la mise à jour asynchrone des statistiques associées à chaque ville.</p>
 */
public class CityStatisticsManager extends Feature implements HasDatabase {

    /**
     * Map des statistiques par ville (clé : identifiant de la ville, valeur : ensemble de statistiques).
     */
    public final HashMap<UUID, Set<CityStatistics>> cityStatistics = new HashMap<>();

    private final DBTable<CityStatistics, String> statisticsTable = DBTable.of(CityStatistics.class);

    /**
     * Initialise le gestionnaire des statistiques des villes en chargeant les statistiques depuis la base de données.
     */
    @Override
    public void onEnable() {
        loadCityStatistics();
    }

    @Override
    public void onDisable() {
        saveCityStatistics();
    }

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(statisticsTable);
    }

    /**
     * Charge toutes les statistiques depuis la base de données.
     */
    public void loadCityStatistics() {
        cityStatistics.clear();
        cityStatistics.putAll(statisticsTable.loadMapSet(CityStatistics::getCityUUID));
    }

    /**
     * Sauvegarde toutes les statistiques dans la base de données.
     */
    public void saveCityStatistics() {
        statisticsTable.saveMapSet(cityStatistics);
        cityStatistics.clear();
    }

    /**
     * Retourne l'ensemble des statistiques pour une ville avec laquelle en crée un nouveau.
     *
     * @param cityUUID l'identifiant de la ville
     * @return l'ensemble des statistiques associées à la ville
     */
    private Set<CityStatistics> getOrCreate(UUID cityUUID) {
        return cityStatistics.computeIfAbsent(cityUUID, k -> new HashSet<>());
    }

    /**
     * Retourne ou crée une statistique pour une ville et un scope donnés.
     *
     * @param cityUUID l'identifiant de la ville
     * @param scope    le scope de la statistique
     * @return la statistique correspondante
     */
    public CityStatistics getOrCreateStat(UUID cityUUID, String scope) {
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
    public void setStat(UUID cityUUID, String scope, Serializable value) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        stat.setValue(value);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                statisticsTable.createOrUpdate(stat);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Supprime toutes les statistiques d'une ville et les efface de la base de données.
     *
     * @param cityUUID l'identifiant de la ville
     */
    public void removeStats(UUID cityUUID) {
        if (!cityStatistics.containsKey(cityUUID)) return;
        cityStatistics.remove(cityUUID);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                DeleteBuilder<CityStatistics, String> statisticsDelete = statisticsTable.deleteBuilder();
                statisticsDelete.where().eq("city_uuid", cityUUID);
                statisticsTable.delete(statisticsDelete.prepare());
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
    public void increment(UUID cityUUID, String scope, long amount) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        long current = stat.asLong();
        stat.setValue(current + amount);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                statisticsTable.createOrUpdate(stat);
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
    public Object getStatValue(UUID cityUUID, String scope) {
        CityStatistics stat = getOrCreateStat(cityUUID, scope);
        return stat.getValue();
    }
}
