package fr.openmc.core.features.city.sub.rank;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;

import java.sql.SQLException;
import java.util.List;

public class CityRankManager extends Feature implements HasDatabase {

	private final CityManager cityManager;
	private Dao<DBCityRank, String> ranksDao;

	public CityRankManager(CityManager cityManager) {
		this.cityManager = cityManager;
	}

	@Override
	public void init() {
		loadRanks();
	}
	
	/**
	 * Initialize the database table for city ranks and set up the DAO.
	 *
	 * @param connectionSource The connection source to the database.
	 * @throws SQLException If there is an error creating the table or DAO.
	 */
	@Override
	public void initDB(ConnectionSource connectionSource) throws SQLException {
		TableUtils.createTableIfNotExists(connectionSource, DBCityRank.class);
		ranksDao = DaoManager.createDao(connectionSource, DBCityRank.class);
	}
	
	/**
	 * Remove all ranks associated with a city from the database.
	 *
	 * @param city The city whose ranks should be removed.
	 * @throws SQLException If there is an error during the deletion process.
	 */
	public void removeRanks(City city) throws SQLException {
		DeleteBuilder<DBCityRank, String> ranksDelete = ranksDao.deleteBuilder();
		ranksDelete.where().eq("city_uuid", city.getUniqueId());
		ranksDao.delete(ranksDelete.prepare());
	}
	
	/**
	 * Add a city rank to the database
	 *
	 * @param rank The rank to add
	 */
	public void addCityRank(DBCityRank rank) {
		try {
			ranksDao.create(rank);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Remove a city rank from the database
	 *
	 * @param rank The rank to remove
	 */
	public void removeCityRank(DBCityRank rank) {
		try {
			DeleteBuilder<DBCityRank, String> delete = ranksDao.deleteBuilder();
			delete.where().eq("city_uuid", rank.getCityUUID()).and().eq("name", rank.getName());
			ranksDao.delete(delete.prepare());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Update a city rank in the database
	 *
	 * @param rank The rank to update
	 */
	public void updateCityRank(DBCityRank rank) {
		try {
			ranksDao.update(rank);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Load city ranks from the database and add them to the city
	 *
	 * @param city The city to load ranks for
	 */
	public void loadCityRanks(City city) {
		try {
			QueryBuilder<DBCityRank, String> query = ranksDao.queryBuilder();
			query.where().eq("city_uuid", city.getUniqueId());
			List<DBCityRank> dbRanks = ranksDao.query(query.prepare());
			
			for (DBCityRank dbRank : dbRanks) {
				city.getRanks().add(dbRank);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Create a copy of a city rank.
	 *
	 * @param rank The rank to copy.
	 * @return A new instance of DBCityRank with the same properties as the original.
	 */
	public DBCityRank copy(DBCityRank rank) {
		return new DBCityRank(rank.getRankUUID(), rank.getCityUUID(), rank.getPriority(), rank.getName(), rank.getIcon(), rank.getPermissionsSet(), rank.getMembersSet());
	}
	
	/**
	 * Load all city ranks from the database and associate them with their respective cities.
	 */
	public void loadRanks() {
		try {
			for (DBCityRank rank : ranksDao.queryForAll()) {
				City city = cityManager.getCity(rank.getCityUUID());
				if (city != null) city.getRanks().add(rank);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
