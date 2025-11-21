package fr.openmc.core.features.corporation.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.models.Shop;

import java.sql.SQLException;

public class ShopDatabaseManager {

	private static Dao<Shop, String> shopDao;
	
	public static void initDB(ConnectionSource connectionSource) throws SQLException {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Shop.class);
			shopDao = DaoManager.createDao(connectionSource, Shop.class);
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public static boolean saveShop(Shop shop) {
		try {
			shopDao.createOrUpdate(shop);
			return true;
		} catch (SQLException e) {
			OMCPlugin.getInstance().getSLF4JLogger().error("Failed to save shop for owner UUID: {}", shop.getOwnerUUID(), e);
			return false;
		}
	}
	
	public static boolean deleteShop(Shop shop) {
		try {
			shopDao.delete(shop);
			return true;
		} catch (SQLException e) {
			OMCPlugin.getInstance().getSLF4JLogger().error("Failed to delete shop for owner UUID: {}", shop.getOwnerUUID(), e);
			return false;
		}
	}
}
