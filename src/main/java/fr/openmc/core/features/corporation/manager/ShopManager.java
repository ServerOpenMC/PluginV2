package fr.openmc.core.features.corporation.manager;

import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.ShopFurniture;
import fr.openmc.core.features.corporation.models.Shop;
import fr.openmc.core.utils.world.WorldUtils;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ShopManager {
	
	@Getter
	private static final Map<UUID, Shop> playerShops = new HashMap<>();
    private static Map<Location, Shop> shopsByLocation;
    
    public static void init() {
		loadShops();
    }
	
	public static void shutdown() {
		saveShops();
	}
	
	public static boolean loadShops() {
		if (shopsByLocation != null) shopsByLocation.clear();
		OMCPlugin.getInstance().getSLF4JLogger().info("Loading shops from database...");
		shopsByLocation = ShopDatabaseManager.loadShops();
		if (shopsByLocation == null) {
			OMCPlugin.getInstance().getSLF4JLogger().error("Failed to initialize ShopManager due to database load failure. No shops loaded.");
			shopsByLocation = new HashMap<>();
			return false;
		}
		shopsByLocation.values().forEach(shop -> setPlayerShop(shop.getOwnerUUID(), shop));
		OMCPlugin.getInstance().getSLF4JLogger().info("Loaded {} shops from database.", shopsByLocation.size());
		return true;
	}
	
	public static boolean loadShopFor(OfflinePlayer player) {
		OMCPlugin.getInstance().getSLF4JLogger().info("Loading shop for player {} from database...", player.getName());
		Shop shop = ShopDatabaseManager.loadShopFor(player.getUniqueId());
		if (shop == null) {
			OMCPlugin.getInstance().getSLF4JLogger().info("No shop found for player {}.", player.getName());
			return false;
		}
		Location loc = new Location(Bukkit.getWorld("world"), shop.getX(), shop.getY(), shop.getZ());
		shopsByLocation.put(loc, shop);
		setPlayerShop(player.getUniqueId(), shop);
		OMCPlugin.getInstance().getSLF4JLogger().info("Loaded shop for player {}.", player.getName());
		return true;
	}
	
	public static boolean saveShops() {
		OMCPlugin.getInstance().getSLF4JLogger().info("Saving all shops to database...");
		for (Shop shop : playerShops.values()) {
			if (!ShopDatabaseManager.saveShop(shop)) {
				OMCPlugin.getInstance().getSLF4JLogger().error("Failed to save all shops to database.");
				return false;
			}
		}
		OMCPlugin.getInstance().getSLF4JLogger().info("All shops saved to database successfully. {} shops saved.", playerShops.size());
		return true;
	}
	
	public static boolean saveShopFor(OfflinePlayer player) {
		OMCPlugin.getInstance().getSLF4JLogger().info("Saving shop for player {} to database...", player.getName());
		Shop shop = getPlayerShop(player.getUniqueId());
		if (shop == null) {
			OMCPlugin.getInstance().getSLF4JLogger().info("No shop found for player {} to save.", player.getName());
			return false;
		}
		if (!ShopDatabaseManager.saveShop(shop)) {
			OMCPlugin.getInstance().getSLF4JLogger().error("Failed to save shop for player {} to database.", player.getName());
			return false;
		}
		OMCPlugin.getInstance().getSLF4JLogger().info("Shop for player {} saved to database successfully.", player.getName());
		return true;
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
        
        Block cashBlock = multiblock.cashBlock().getBlock();
		
		shopsByLocation.put(shop.getLocation(), shop);
        
        if (ItemsAdderHook.isHasItemAdder())
            if (!ShopFurniture.placeShopFurniture(cashBlock, WorldUtils.getYaw(player))) cashBlock.setType(Material.OAK_SIGN);
		else cashBlock.setType(Material.OAK_SIGN);
		
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
        if (multiblock == null) return false;
	    
	    World world = Bukkit.getWorld("world");
		if (world == null) {
			OMCPlugin.getInstance().getSLF4JLogger().error("World 'world' not found while removing shop at location: {}", shop.getLocation());
			return false;
		}
        
        Block cashBlock = world.getBlockAt(multiblock.cashBlock());
        Block stockBlock = world.getBlockAt(multiblock.stockBlock());

        if (ItemsAdderHook.isHasItemAdder()) {
            if (!ShopFurniture.hasFurniture(cashBlock)) return false;
            if (!ShopFurniture.removeShopFurniture(cashBlock)) return false;
        }
        else if ((cashBlock.getType() != Material.OAK_SIGN && cashBlock.getType() != Material.BARRIER) || stockBlock.getType() != Material.BARREL) return false;
	    cashBlock.setType(Material.AIR); // Remove sign or furniture block
        stockBlock.setType(Material.AIR); // Remove barrel block
        
        // Async cleanup of location mappings
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                shopsByLocation.entrySet().removeIf(entry -> entry.getValue().getOwnerUUID().equals(shop.getOwnerUUID())));
        return true;
    }
	
	public static Set<Shop> getAllShops() {
		return Set.copyOf(shopsByLocation.values());
	}
	
	public static Shop getPlayerShop(UUID playerUUID) {
		return playerShops.get(playerUUID);
	}
	
	public static void setPlayerShop(UUID playerUUID, Shop shop) {
		playerShops.put(playerUUID, shop);
	}
	
	/**
	 * Check if a player has a shop
	 *
	 * @param playerUUID the UUID of the player to check
	 * @return true if a shop is found
	 */
	public static boolean hasShop(UUID playerUUID) {
		return getPlayerShop(playerUUID) != null;
	}
}
