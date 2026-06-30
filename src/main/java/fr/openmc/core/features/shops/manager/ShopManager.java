package fr.openmc.core.features.shops.manager;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.shops.ShopFurniture;
import fr.openmc.core.features.shops.commands.ShopCommand;
import fr.openmc.core.features.shops.listener.ShopListener;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopSale;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.world.WorldUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Credit(developers = {"gab400", "Nocolm", "Xernas78"}, graphist = {"Gexary"})
public class ShopManager extends Feature implements LoadAfterItemsAdder, DatabaseFeature, HasListeners, HasCommands {
	
	@Getter
	private static final Map<UUID, Shop> shops = new HashMap<>();
    private static Map<Location, Shop> shopsByLocation;
	
	@Override
	protected void init() {
		loadShops();
		loadShopItems();
		loadSalesItems();
	}
	
	@Override
	protected void save() {
		saveShops();
		saveShopItems();
		saveShopSales();
	}
	
	@Override
	public void initDB(ConnectionSource connectionSource) throws SQLException {
		ShopDatabaseManager.initDB(connectionSource);
	}
	
	@Override
	public Set<Object> getCommands() {
		return Set.of(new ShopCommand());
	}
	
	@Override
	public Set<Listener> getListeners() {
		return Set.of(new ShopListener());
	}
	
	/**
	 * Load shops in the map from DB
	 *
	 * @return true if shops are successfully loaded, false otherwise
	 */
	public static boolean loadShops() {
		if (shopsByLocation != null) shopsByLocation.clear();
		try {
			shopsByLocation = ShopDatabaseManager.loadDBShops();
		} catch (SQLException e) {
			OMCLogger.error("Cannot save shops from database:\n" + e.getMessage());
			return false;
		}
		
		shopsByLocation.values().forEach(shop -> setUUIDShop(shop.getShopUUID(), shop));
		return true;
	}
	
	/**
	 * Load shops items from DB
	 *
	 * @return true if items are successfully loaded, false otherwise
	 */
	public static boolean loadShopItems() {
		try {
			ShopDatabaseManager.loadDBShopItems();
			return true;
		} catch (SQLException e) {
			OMCLogger.error("Cannot save shop items from database:\n" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Load shops sales from DB
	 *
	 * @return true if sales are successfully loaded, false otherwise
	 */
	public static boolean loadSalesItems() {
		try {
			ShopDatabaseManager.loadDBShopSales();
			return true;
		} catch (SQLException e) {
			OMCLogger.error("Cannot save shop sales from database:\n" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Save shops to DB
	 */
	public static void saveShops() {
		for (Shop shop : shops.values()) {
			if (ShopDatabaseManager.saveDBShop(shop)) continue;
			OMCLogger.error("Failed to save " + shop.getName() + " to database.");
		}
	}
	
	/**
	 * Save shops items to DB
	 */
	public static void saveShopItems() {
		for (Shop shop : shops.values()) {
			if (!shop.hasItem()) continue;
			if (ShopDatabaseManager.saveDBShopItem(shop.getItem())) continue;
			OMCLogger.error("Failed to save " + shop.getName() + " item to database.");
		}
	}
	
	/**
	 * Save shops items to DB
	 */
	public static void saveShopSales() {
		for (Shop shop : shops.values()) {
			if (shop.getSales().isEmpty()) continue;
			boolean error = false;
			for (ShopSale s : shop.getSales()) {
				if (ShopDatabaseManager.saveDBShopSale(s)) continue;
				error = true;
			}
			if (error) OMCLogger.error("Failed to save " + shop.getName() + " sales to database.");
		}
	}

    /**
     * Retrieves a shop located at a given location.
     *
     * @param location The location to check.
     * @return The shop found at that location, or null if none exists.
     */
    public static Shop getShopAt(Location location) {
        return shopsByLocation.get(location.setRotation(0, 0));
    }
	
	/**
	 * Retrieves a shop located at a given location.
	 *
	 * @param x The x-coordinate of the location.
	 * @param y The y-coordinate of the location.
	 * @param z The z-coordinate of the location.
	 * @return The shop found at that location, or null if none exists.
	 */
	public static Shop getShopAt(int x, int y, int z) {
		return shopsByLocation.get(new Location(Bukkit.getWorld("world"), x, y, z));
	}

    /**
     * Places the shop block (sign or ItemsAdder furniture) in the world,
     * oriented based on the player's direction.
     *
     * @param player The player placing the shop.
     * @param shop The shop to place.
     * @return true if successfully placed, false otherwise.
     */
    public static boolean placeShop(Player player, Shop shop) {
        Shop.Multiblock multiblock = shop.getMultiblock();
        if (multiblock == null) return false;
        
        Block cashBlock = multiblock.cashBlockLoc().getBlock();
		
		shopsByLocation.put(shop.getLocation(), shop);
		shops.put(shop.getShopUUID(), shop);
        
        if (ItemsAdderHook.isEnable()) {
	        if (!ShopFurniture.placeShopFurniture(cashBlock, WorldUtils.getYaw(player))) cashBlock.setType(Material.OAK_SIGN);
        } else {
			cashBlock.setType(Material.OAK_SIGN);
        }
		
        return true;
    }

    /**
     * Removes a shop from the world and unregisters its multiblock structure.
     * Handles both ItemsAdder and fallback vanilla types.
     *
     * @param shop The shop to remove.
     * @return true if successfully removed, false otherwise.
     */
    public static boolean removeShop(Shop shop) {
        Shop.Multiblock multiblock = shop.getMultiblock();
        if (multiblock == null) {
	        OMCLogger.error("Multiblock for {} is null!", shop.getName());
			return false;
        }
	    
	    World world = Bukkit.getWorld("world");
		if (world == null) {
			OMCLogger.error("World 'world' not found while removing {} at location: {}", shop.getName(), shop.getLocation());
			return false;
		}
        
        Block cashBlock = world.getBlockAt(multiblock.cashBlockLoc());
        Block stockBlock = world.getBlockAt(multiblock.stockBlockLoc());

        if (ItemsAdderHook.isEnable()) {
            if (ShopFurniture.hasFurniture(cashBlock)) {
				if (!ShopFurniture.removeShopFurniture(cashBlock)) {
					OMCLogger.warn("Cannot remove furniture for " + shop.getName());
					return false;
				}
            } else if ((cashBlock.getType() != Material.OAK_SIGN && cashBlock.getType() != Material.BARRIER) || stockBlock.getType() != Material.BARREL) {
				OMCLogger.warn("Bad multiblock for " + shop.getName());
				return false;
            }
			else OMCLogger.warn(shop.getName() + " has no furniture detected.");
        } else if ((cashBlock.getType() != Material.OAK_SIGN && cashBlock.getType() != Material.BARRIER) || stockBlock.getType() != Material.BARREL) {
	        OMCLogger.warn("Bad multiblock for " + shop.getName());
			return false;
        }
	    cashBlock.setType(Material.AIR); // Remove sign or furniture block
        stockBlock.setType(Material.AIR); // Remove barrel block
        
        // Async cleanup of location mappings
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
			        shopsByLocation.entrySet().removeIf(entry -> entry.getValue().getOwnerUUID().equals(shop.getOwnerUUID()));
					shops.remove(shop.getShopUUID());
		        });
        return true;
    }
	
	/**
	 * Returns all registered shops
	 *
	 * @return a set of shops
	 */
	public static Set<Shop> getAllShops() {
		return Set.copyOf(shops.values());
	}
	
	public static Shop getShopByUUID(UUID shopUUID) {
		return shops.get(shopUUID);
	}
	
	/**
	 * Assign a shop to a player if any shop was already assigned
	 *
	 * @param shopUUID the UUID of the player
	 * @param shop the shop
	 */
	public static void setUUIDShop(UUID shopUUID, Shop shop) {
		shops.put(shopUUID, shop);
	}
	
	/**
	 * Check if a player has a shop
	 *
	 * @param playerUUID the UUID of the player to check
	 * @return true if a shop is found
	 */
	public static boolean hasShop(UUID playerUUID) {
		for (Shop shop : shops.values()) {
			if (shop.isOwner(playerUUID)) return true;
		}
		return false;
	}
	
	/**
	 * Verifies if the player is the owner of the shop
	 *
	 * @param player the player to verify
	 * @param shop the shop to verify
	 * @return true if the player is the owner of the shop, false otherwise
	 */
	public static boolean isShopOwner(Player player, Shop shop) {
		return isShopOwner(player.getUniqueId(), shop);
	}
	
	/**
	 * Verifies if the player is the owner of the shop
	 *
	 * @param playerUUID the uuid of the player to verify
	 * @param shop the shop to verify
	 * @return true if the player is the owner of the shop, false otherwise
	 */
	public static boolean isShopOwner(UUID playerUUID, Shop shop) {
		return shop.getOwnerUUID().equals(playerUUID);
	}
}
