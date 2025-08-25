package fr.openmc.core.features.city.sub.rank;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.CityRank;

import java.sql.SQLException;
import java.util.List;

public class CityRankManager {

    private static Dao<CityRank, String> ranksDao;

    public CityRankManager() {
        loadRanks();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityRank.class);
        ranksDao = DaoManager.createDao(connectionSource, CityRank.class);
    }

    public void loadRanks() {
        try {
            ranksDao.queryForAll()
                    .forEach(rank -> {
                        City city = CityManager.getCity(rank.getCityUUID());
                        if (city != null) {
                            city.getRanks().add(rank);
                        }
                    });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeRanks(City city) throws SQLException {
        DeleteBuilder<CityRank, String> ranksDelete = ranksDao.deleteBuilder();
        ranksDelete.where().eq("city_uuid", city.getUUID());
        ranksDao.delete(ranksDelete.prepare());
    }

    /**
     * Add a city rank to the database
     *
     * @param rank The rank to add
     */
    public static void addCityRank(CityRank rank) {
        try {
            ranksDao.create(rank);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a city rank from the database
     *
     * @param rank The rank to remove
     */
    public static void removeCityRank(CityRank rank) {
        try {
            DeleteBuilder<CityRank, String> delete = ranksDao.deleteBuilder();
            delete.where().eq("city_uuid", rank.getCityUUID()).and().eq("name", rank.getName());
            ranksDao.delete(delete.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a city rank in the database
     *
     * @param rank The rank to update
     */
    public static void updateCityRank(CityRank rank) {
        try {
            ranksDao.update(rank);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load city ranks from the database and add them to the city
     *
     * @param city The city to load ranks for
     */
    public static void loadCityRanks(City city) {
        try {
            QueryBuilder<CityRank, String> query = ranksDao.queryBuilder();
            query.where().eq("city_uuid", city.getUUID());
            List<CityRank> dbRanks = ranksDao.query(query.prepare());

            for (CityRank dbRank : dbRanks) {
                city.getRanks().add(dbRank);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
