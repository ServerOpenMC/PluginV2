package fr.openmc.core.disabled.corporation.manager;

import fr.openmc.core.disabled.corporation.MethodState;
import fr.openmc.core.disabled.corporation.shops.Shop;
import fr.openmc.core.disabled.corporation.shops.ShopOwner;
import fr.openmc.core.features.economy.EconomyManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerShopManager {

    @Getter
    private static final Map<UUID, Shop> playerShops = new HashMap<>();

    /**
     * create a shop
     *
     * @param playerUUID   the uuid of the player who creates it
     * @param barrel       the barrel block
     * @param cashRegister the cash register
     * @param shop_uuid    the uuid of the shop if it already has one
     * @return true if the shop has been created
     */
    public static boolean createShop(UUID playerUUID, Block barrel, Block cashRegister, UUID shop_uuid) {
        if (!EconomyManager.withdrawBalance(playerUUID, 500) && shop_uuid==null) {
            return false;
        }
        Shop newShop;
        if (shop_uuid != null) {
            newShop = new Shop(new ShopOwner(playerUUID), 0, shop_uuid);
        } else {
            newShop = new Shop(new ShopOwner(playerUUID), 0);
        }

        playerShops.put(playerUUID, newShop);
        CompanyManager.shops.add(newShop);
        ShopBlocksManager.registerMultiblock(newShop,
                new Shop.Multiblock(barrel.getLocation(), cashRegister.getLocation()));
        if (shop_uuid == null) {
            ShopBlocksManager.placeShop(newShop, Bukkit.getPlayer(playerUUID), false);
        }
        return true;
    }

    /**
     * delete a shop
     *
     * @param playerUUID the uuid of the player who deletes the shop
     * @return a Methode state
     */
    public static MethodState deleteShop(UUID playerUUID) {
        Shop shop = getPlayerShop(playerUUID);
        if (!shop.getItems().isEmpty()) {
            return MethodState.WARNING;
        }
        if (!ShopBlocksManager.removeShop(shop)) {
            return MethodState.ESCAPE;
        }
        playerShops.remove(playerUUID);
        CompanyManager.shops.remove(shop);
        EconomyManager.addBalance(playerUUID, 400);
        return MethodState.SUCCESS;
    }

    /**
     * get a shop from the uuid of a player
     *
     * @param playerUUID the uuid we check
     * @return a shop if found
     */
    public static Shop getPlayerShop(UUID playerUUID) {
        return playerShops.get(playerUUID);
    }

    /**
     * get a shop from a shop uuid
     *
     * @param shop_uuid the uuid we check
     * @return a shop if found
     */
    public static Shop getShopByUUID(UUID shop_uuid) {
        return playerShops.values().stream().filter(shop -> shop.getUuid().equals(shop_uuid)).findFirst().orElse(null);
    }

    /**
     * know if a player has a shop
     *
     * @param playerUUID the player to check
     * @return true if a shop is found
     */
    public static boolean hasShop(UUID playerUUID) {
        return getPlayerShop(playerUUID) != null;
    }
}
