package fr.openmc.core.features.shops.managers;

import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopItem;
import fr.openmc.core.features.shops.models.ShopSale;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jspecify.annotations.NonNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopDatabaseManager extends Feature implements HasDatabase {

	private final DBTable<Shop, UUID> shopTable = DBTable.of(Shop.class);
	private final DBTable<ShopItem, UUID> shopItemTable = DBTable.of(ShopItem.class);
	private final DBTable<ShopSale, UUID> shopSaleTable = DBTable.of(ShopSale.class);

	@Override
	public List<DBTable<?, ?>> getTables() {
		return List.of(shopTable, shopItemTable, shopSaleTable);
	}

	/**
	 * Loads all shops from the database and organizes them by their location.
	 *
	 * @return A map where each location is associated with its corresponding shop.
	 * @throws SQLException if there is an error during the database query.
	 */
	public @NonNull Map<Location, Shop> loadDBShops() throws SQLException {
		Map<Location, Shop> shopsByLocation = new HashMap<>();
		
		List<Shop> shops = shopTable.queryForAll();
		for (Shop shop : shops) {
			Location loc = new Location(Bukkit.getWorld("world"), shop.getX(), shop.getY(), shop.getZ());
			if (shop.getMultiblock() == null) {
				if (!shop.setMultiblock(new Shop.Multiblock(loc, loc.clone().add(0, 1, 0)))) {
					OMCLogger.error("Cannot set multiblock for {} {}, but shop is registered", shop.getName(), shop.getShopUUID());
				}
			}
			shopsByLocation.put(loc, shop);
		}
		return shopsByLocation;
	}
	
	/**
	 * Loads all shop items from the database and associates them with their corresponding shops.
	 *
	 * @throws SQLException if a database access error occurs during querying or processing.
	 */
	public void loadDBShopItems() throws SQLException {
		List<ShopItem> shopItems = shopItemTable.queryForAll();
		for (ShopItem item : shopItems) {
			Shop shop = item.getShop();
			if (shop == null) {
				OMCLogger.error("Shop for item with shopUUID " + item.getShopUUID() + " is null, item not assigned");
				continue;
			}
			shop.setItem(item.deserialize().initMaxAmount());
		}
	}
	
	/**
	 * Loads all shop sales from the database and associates them with their corresponding shops.
	 *
	 * @throws SQLException if a database access error occurs while querying sales.
	 */
	public void loadDBShopSales() throws SQLException {
		List<ShopSale> shopSales = shopSaleTable.queryForAll();
		for (ShopSale sale : shopSales) {
			Shop shop = sale.getShop();
			if (shop == null) {
				OMCLogger.error("Shop for sale with shopUUID " + sale.getShopUUID() + " is null, sale not assigned");
				continue;
			}
			sale.registerVariables();
			shop.registerSale(sale);
		}
	}
	
	/**
	 * Saves a shop in DB
	 *
	 * @param shop shop to save
	 * @return true if the shop was saved, false if a database exception occurred
	 */
	public boolean saveDBShop(Shop shop) {
		try {
			shopTable.createOrUpdate(shop);
			return true;
		} catch (SQLException e) {
			OMCLogger.error("Failed to save shop for owner UUID: {}\nCause: {}", shop.getOwnerUUID(), e.getMessage());
			return false;
		}
	}
	
	/**
	 * Saves the given shop item object to the database.
	 * The ShopItem is serialized before being persisted.
	 *
	 * @param item the shop item instance to save in the database
	 * @return true if the shop item was successfully saved, false if a database
	 *         exception occurred
	 */
	public boolean saveDBShopItem(ShopItem item) {
		try {
			shopItemTable.createOrUpdate(item.serialize());
			return true;
		} catch (SQLException e) {
			OMCLogger.error("Failed to save shop item for owner UUID: {}\nCause: {}", item.getShop().getOwnerUUID(), e.getMessage());
			return false;
		}
	}
	
	/**
	 * Saves a sale in DB
	 *
	 * @param sale the sale to save
	 * @return true if the sale was saved, false if a database exception occurred
	 */
	public boolean saveDBShopSale(ShopSale sale) {
		try {
			shopSaleTable.createOrUpdate(sale);
			return true;
		} catch (SQLException e) {
			OMCLogger.error("Failed to save shop sale for owner UUID: {}\nCause: {}", sale.getShop().getOwnerUUID(), e.getMessage());
			return false;
		}
	}
	
	/**
	 * Deletes a shop and its associated data (items and sales) from the database.
	 *
	 * @param shop the shop instance to be deleted, including its related items and sales
	 * @return true if the shop and its associated data were successfully deleted, false if a database exception occurred
	 */
	public boolean deleteDBShop(Shop shop) {
		try {
			shopTable.delete(shop);
			shopItemTable.delete(shop.getItem());
			shopSaleTable.delete(shop.getSales());
			return true;
		} catch (SQLException e) {
			OMCLogger.error("Failed to delete shop for owner UUID: {}\nCause: {}", shop.getOwnerUUID(), e.getMessage());
			return false;
		}
	}
}
