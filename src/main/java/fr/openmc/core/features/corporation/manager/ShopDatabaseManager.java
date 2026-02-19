package fr.openmc.core.features.corporation.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.models.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopDatabaseManager {

	private static Dao<Shop, UUID> shopDao;
	
	public static void initDB(ConnectionSource connectionSource) throws SQLException {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Shop.class);
			shopDao = DaoManager.createDao(connectionSource, Shop.class);
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}
	
	public static @Nullable Map<Location, Shop> loadShops() {
		Map<Location, Shop> shopsByLocation = new HashMap<>();
		try {
			List<Shop> shops = shopDao.queryForAll();
			for (Shop shop : shops) {
				Location loc = new Location(Bukkit.getWorld("world"), shop.getX(), shop.getY(), shop.getZ());
				shopsByLocation.put(loc, shop);
			}
		} catch (SQLException e) {
			OMCPlugin.getInstance().getSLF4JLogger().error("Failed to load shops from database", e);
			return null;
		}
		return shopsByLocation;
	}
	
	public static @Nullable Shop loadShopFor(UUID ownerUUID) {
		try {
			return shopDao.queryForId(ownerUUID);
		} catch (SQLException e) {
			OMCPlugin.getInstance().getSLF4JLogger().error("Failed to load shop for owner UUID: {}", ownerUUID, e);
			return null;
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
