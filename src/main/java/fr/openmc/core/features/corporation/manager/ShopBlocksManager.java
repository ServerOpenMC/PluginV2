package fr.openmc.core.features.corporation.manager;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.ItemsAdderIntegration;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.world.WorldUtils;
import fr.openmc.core.utils.world.Yaw;
import lombok.Getter;
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

    private final Map<UUID, Shop.Multiblock> multiblocks = new HashMap<>();
    private final Map<Location, Shop> shopsByLocation = new HashMap<>();

    private final OMCPlugin plugin;
    @Getter static ShopBlocksManager instance;

    public ShopBlocksManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    /**
     * Registers a shop's multiblock structure and maps its key locations.
     *
     * @param shop The shop to register.
     * @param multiblock The multiblock structure associated with the shop.
     */
    public void registerMultiblock(Shop shop, Shop.Multiblock multiblock) {
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
    public Shop.Multiblock getMultiblock(UUID uuid) {
        return multiblocks.get(uuid);
    }

    /**
     * Retrieves a shop located at a given location.
     *
     * @param location The location to check.
     * @return The shop found at that location, or null if none exists.
     */
    public Shop getShop(Location location) {
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
    public void placeShop(Shop shop, Player player, boolean isCompany) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Yaw yaw = WorldUtils.getYaw(player);

        if (CustomItemRegistry.hasItemsAdder()) {
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
    public boolean removeShop(Shop shop) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return false;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Block stockBlock = multiblock.getStockBlock().getBlock();

        if (CustomItemRegistry.hasItemsAdder()){

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
        }.runTaskAsynchronously(plugin);
        return true;
    }

}