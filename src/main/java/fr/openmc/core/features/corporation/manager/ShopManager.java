package fr.openmc.core.features.corporation.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.ItemsAdderIntegration;
import fr.openmc.core.features.corporation.models.DBShopItem;
import fr.openmc.core.features.corporation.models.DBShopLocation;
import fr.openmc.core.features.corporation.models.DBShopSale;
import fr.openmc.core.features.corporation.models.ShopSupplier;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopManager {

    private static final Map<UUID, Shop.Multiblock> multiblocks = new HashMap<>();
    private static final Map<Location, Shop> shopsByLocation = new HashMap<>();
    
    private static Dao<DBShopLocation, UUID> shopLocationDao;
    private static Dao<DBShopItem, UUID> shopItemsDao;
    private static Dao<DBShopSale, UUID> shopSalesDao;
    private static Dao<ShopSupplier, UUID> shopSuppliersDao;

    /**
     * Registers a shop's multiblock structure and maps its key locations.
     *
     * @param shop The shop to register.
     * @param multiblock The multiblock structure associated with the shop.
     */
    public static void registerMultiblock(Shop shop, Shop.Multiblock multiblock) {
        multiblocks.put(shop.getOwnerUUID(), multiblock);
        Location stockLoc = multiblock.stockBlock();
        Location cashLoc = multiblock.cashBlock();
        shopsByLocation.put(stockLoc, shop);
        shopsByLocation.put(cashLoc, shop);
    }

    /**
     * Retrieves the multiblock structure associated with a given UUID.
     *
     * @param uuid The UUID of the shop.
     * @return The multiblock structure if it exists, otherwise null.
     */
    public static Shop.Multiblock getMultiblock(UUID uuid) {
        return multiblocks.get(uuid);
    }

    /**
     * Retrieves a shop located at a given location.
     *
     * @param location The location to check.
     * @return The shop found at that location, or null if none exists.
     */
    public static Shop getShopAt(Location location) {
        return shopsByLocation.get(location);
    }

    /**
     * Places the shop block (sign or ItemsAdder furniture) in the world,
     * oriented based on the player's direction.
     *
     * @param player The player placing the shop.
     * @param shop The shop to place.
     */
    public static boolean placeShop(Player player, Shop shop) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getOwnerUUID());
        if (multiblock == null) return false;
        
        Block cashBlock = multiblock.cashBlock().getBlock();
        
        if (ItemsAdderHook.isHasItemAdder()) {
            if (!ItemsAdderIntegration.placeShopFurniture(cashBlock, WorldUtils.getYaw(player)))
                cashBlock.setType(Material.OAK_SIGN);
        } else cashBlock.setType(Material.OAK_SIGN);
        
        return true;
    }

    /**
     * Removes a shop from the world and unregisters its multiblock structure.
     * Handles both ItemsAdder and fallback vanilla types.
     *
     * @param shop The shop to remove.
     * @return True if successfully removed, false otherwise.
     */
    public static boolean removeShop(Shop shop) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getOwnerUUID());
        if (multiblock == null) return false;
        
        Block cashBlock = multiblock.cashBlock().getBlock();
        Block stockBlock = multiblock.stockBlock().getBlock();

        if (ItemsAdderHook.isHasItemAdder()) {
            if (!ItemsAdderIntegration.hasFurniture(cashBlock)) return false;
            if (!ItemsAdderIntegration.removeShopFurniture(cashBlock)) return false;
        } else {
            if ((cashBlock.getType() != Material.OAK_SIGN && cashBlock.getType() != Material.BARRIER) || stockBlock.getType() != Material.BARREL) {
                return false;
            }
        }

        stockBlock.setType(Material.AIR); // Remove barrel block
        
        // Async cleanup of location mappings
        multiblocks.remove(shop.getOwnerUUID());
        cashBlock.setType(Material.AIR); // Remove sign or furniture block
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                shopsByLocation.entrySet().removeIf(entry -> entry.getValue().getOwnerUUID().equals(shop.getOwnerUUID())));
        return true;
    }
    
    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBShopLocation.class);
        shopLocationDao = DaoManager.createDao(connectionSource, DBShopLocation.class);
        
        TableUtils.createTableIfNotExists(connectionSource, DBShopSale.class);
        shopSalesDao = DaoManager.createDao(connectionSource, DBShopSale.class);
        
        TableUtils.createTableIfNotExists(connectionSource, DBShopItem.class);
        shopItemsDao = DaoManager.createDao(connectionSource, DBShopItem.class);
        
        TableUtils.createTableIfNotExists(connectionSource, ShopSupplier.class);
        shopSuppliersDao = DaoManager.createDao(connectionSource, ShopSupplier.class);
    }
    
    public static boolean saveShopLocation(DBShopLocation dbShopLocation) {
	    try {
		    shopLocationDao.createOrUpdate(dbShopLocation);
            return true;
	    } catch (SQLException e) {
		    OMCPlugin.getInstance().getSLF4JLogger().error("Error saving shop location for owner UUID: " + dbShopLocation.getOwnerUUID().toString(), e);
            return false;
	    }
    }
    
    public static boolean deleteShopLocation(UUID ownerUUID) {
        try {
            shopLocationDao.deleteById(ownerUUID);
            return true;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error deleting shop location for owner UUID: " + ownerUUID.toString(), e);
            return false;
        }
    }
}
