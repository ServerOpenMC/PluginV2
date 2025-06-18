package fr.openmc.core.features.corporation.manager;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.ItemsAdderIntegration;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.api.ItemAdderApi;
import fr.openmc.core.utils.world.WorldUtils;
import fr.openmc.core.utils.world.Yaw;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopBlocksManager {

    private static Map<UUID, Shop.Multiblock> multiblocks = new HashMap<>();
    private static Map<Location, Shop> shopsByLocation = new HashMap<>();

    /**
     * Registers a shop's multiblock structure and maps its key locations.
     *
     * @param shop The shop to register.
     * @param multiblock The multiblock structure associated with the shop.
     */
    public static void registerMultiblock(Shop shop, Shop.Multiblock multiblock) {
        multiblocks.put(shop.getUuid(), multiblock);
        Location stockLoc = multiblock.getStockBlock();
        Location cashLoc = multiblock.getCashBlock();
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
     * @param isCompany Whether the shop belongs to a company (unused here but may be relevant elsewhere).
     */
    public static void placeShop(Shop shop, Player player, boolean isCompany) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Yaw yaw = WorldUtils.getYaw(player);

        if (ItemAdderApi.hasItemAdder()) {
            boolean placed = ItemsAdderIntegration.placeShopFurniture(cashBlock);
            if (!placed) {
                cashBlock.setType(Material.OAK_SIGN);
            }
        } else {
            cashBlock.setType(Material.OAK_SIGN);
        }

        BlockData cashData = cashBlock.getBlockData();
        if (cashData instanceof Directional directional) {
            directional.setFacing(yaw.getOpposite().toBlockFace());
            cashBlock.setBlockData(directional);
        }
    }

    /**
     * Removes a shop from the world and unregisters its multiblock structure.
     * Handles both ItemsAdder and fallback vanilla types.
     *
     * @param shop The shop to remove.
     * @return True if successfully removed, false otherwise.
     */
    public static boolean removeShop(Shop shop) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return false;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Block stockBlock = multiblock.getStockBlock().getBlock();

        if (ItemAdderApi.hasItemAdder()) {

            if (!ItemsAdderIntegration.hasFurniture(cashBlock)) {
                return false;
            }
            if (!ItemsAdderIntegration.removeShopFurniture(cashBlock)){
                return false;
            }

        } else {
            if (cashBlock.getType() != Material.OAK_SIGN && cashBlock.getType() != Material.BARRIER || stockBlock.getType() != Material.BARREL) {
                return false;
            }
        }

        // Async cleanup of location mappings
        multiblocks.remove(shop.getUuid());
        cashBlock.setType(Material.AIR);
        new BukkitRunnable() {
            @Override
            public void run() {
                shopsByLocation.entrySet().removeIf(entry -> entry.getValue().getUuid().equals(shop.getUuid()));
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
        return true;
    }

}
