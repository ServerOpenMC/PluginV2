package fr.openmc.core.features.city.sub.rank;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.models.db.DBCityRank;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class CityRankManager extends Feature implements HasDatabase, HasCommands {
	private final DBTable<DBCityRank, String> ranksTable = DBTable.of(DBCityRank.class);

	@Override
	public void onEnable() {
		loadRanks();
	}

	@Override
	public Set<Object> getCommands() {
		return Set.of(
				new CityRankCommands(OMCRegistry.FEATURES.CITY.get())
		);
	}

	@Override
	public List<DBTable<?, ?>> getTables() {
		return List.of(ranksTable);
	}
	
	/**
	 * Remove all ranks associated with a city from the database.
	 *
	 * @param city The city whose ranks should be removed.
	 * @throws SQLException If there is an error during the deletion process.
	 */
	public void removeRanks(City city) throws SQLException {
		DeleteBuilder<DBCityRank, String> ranksDelete = ranksTable.deleteBuilder();
		ranksDelete.where().eq("city_uuid", city.getUniqueId());
		ranksTable.delete(ranksDelete.prepare());
	}
	
	/**
	 * Add a city rank to the database
	 *
	 * @param rank The rank to add
	 */
	public void addCityRank(DBCityRank rank) {
		try {
			ranksTable.create(rank);
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
			DeleteBuilder<DBCityRank, String> delete = ranksTable.deleteBuilder();
			delete.where().eq("city_uuid", rank.getCityUUID()).and().eq("name", rank.getName());
			ranksTable.delete(delete.prepare());
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
			ranksTable.update(rank);
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
			QueryBuilder<DBCityRank, String> query = ranksTable.queryBuilder();
			query.where().eq("city_uuid", city.getUniqueId());
			List<DBCityRank> dbRanks = ranksTable.query(query.prepare());
			
			for (DBCityRank dbRank : dbRanks) {
				city.getRanks().add(dbRank);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Load all city ranks from the database and associate them with their respective cities.
	 */
	public void loadRanks() {
		try {
			for (DBCityRank rank : ranksTable.queryForAll()) {
				City city = City.of(rank.getCityUUID());
				if (city != null) city.getRanks().add(rank);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
