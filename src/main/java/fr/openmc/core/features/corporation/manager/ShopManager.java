package fr.openmc.core.features.corporation.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.ItemsAdderIntegration;
import fr.openmc.core.features.corporation.models.DBShop;
import fr.openmc.core.features.corporation.models.DBShopItem;
import fr.openmc.core.features.corporation.models.DBShopSale;
import fr.openmc.core.features.corporation.models.ShopSupplier;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.world.WorldUtils;
import fr.openmc.core.utils.world.Yaw;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopManager {

    private static final Map<UUID, Shop.Multiblock> multiblocks = new HashMap<>();
    private static final Map<Location, Shop> shopsByLocation = new HashMap<>();
    
    private static Dao<DBShop, UUID> shopsDao;
    private static Dao<DBShopItem, UUID> itemsDao;
    private static Dao<DBShopSale, UUID> salesDao;
    private static Dao<ShopSupplier, UUID> suppliersDao;

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
    public static Shop getShop(Location location) {
        return shopsByLocation.get(location);
    }

    /**
     * Places the shop block (sign or ItemsAdder furniture) in the world,
     * oriented based on the player's direction.
     *
     * @param shop The shop to place.
     * @param player The player placing the shop.
     */
    public static void placeShop(Shop shop, Player player) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getOwnerUUID());
        if (multiblock == null) return;
        
        Block cashBlock = multiblock.cashBlock().getBlock();
        Yaw yaw = WorldUtils.getYaw(player);

        if (ItemsAdderHook.isHasItemAdder()) {
            if (! ItemsAdderIntegration.placeShopFurniture(cashBlock)) cashBlock.setType(Material.OAK_SIGN);
        } else {
            cashBlock.setType(Material.OAK_SIGN);
        }

        BlockData cashData = cashBlock.getBlockData();
        if (! (cashData instanceof Directional directional)) return;
        directional.setFacing(yaw.getOpposite().toBlockFace());
        cashBlock.setBlockData(directional);
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
            if (! ItemsAdderIntegration.hasFurniture(cashBlock)) return false;
            if (! ItemsAdderIntegration.removeShopFurniture(cashBlock)) return false;
        } else {
            if (cashBlock.getType() != Material.OAK_SIGN && cashBlock.getType() != Material.BARRIER || stockBlock.getType() != Material.BARREL) {
                return false;
            }
        }

        // Async cleanup of location mappings
        multiblocks.remove(shop.getOwnerUUID());
        cashBlock.setType(Material.AIR);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                shopsByLocation.entrySet().removeIf(entry -> entry.getValue().getOwnerUUID().equals(shop.getOwnerUUID())));
        return true;
    }
    
    public static void initDB(ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, DBShop.class);
            shopsDao = DaoManager.createDao(connectionSource, DBShop.class);
            
            TableUtils.createTableIfNotExists(connectionSource, DBShopSale.class);
            salesDao = DaoManager.createDao(connectionSource, DBShopSale.class);
            
            TableUtils.createTableIfNotExists(connectionSource, DBShopItem.class);
            itemsDao = DaoManager.createDao(connectionSource, DBShopItem.class);
            
            TableUtils.createTableIfNotExists(connectionSource, ShopSupplier.class);
            suppliersDao = DaoManager.createDao(connectionSource, ShopSupplier.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
