package fr.openmc.core.features.corporation.manager;

import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.ShopFurniture;
import fr.openmc.core.features.corporation.models.Shop;
import fr.openmc.core.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopManager {

    private static final Map<Location, Shop> shopsByLocation = new HashMap<>();
    
    public static void init() {
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
		return shopsByLocation.get(new Location(Bukkit.getWorld("world"), x, y, z, 0, 0));
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
}
