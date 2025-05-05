package fr.openmc.core.features.corporation;

import fr.openmc.core.features.economy.EconomyManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerShopManager {

    private final Map<UUID, Shop> playerShops = new HashMap<>();
    private final EconomyManager economyManager = EconomyManager.getInstance();
    private final ShopBlocksManager shopBlocksManager = ShopBlocksManager.getInstance();

    @Getter static PlayerShopManager instance;

    public PlayerShopManager() {
        instance = this;
    }

    public boolean createShop(UUID playerUUID, Block barrel, Block cashRegister, UUID shop_uuid) {
        if (!economyManager.withdrawBalance(playerUUID, 500) && shop_uuid==null) {
            return false;
        }
        Shop newShop;
        if (shop_uuid!=null){
            newShop = new Shop(new ShopOwner(playerUUID), 0, shop_uuid);
        } else {
            newShop = new Shop(new ShopOwner(playerUUID), 0);
        }

        playerShops.put(playerUUID, newShop);
        CompanyManager.shops.add(newShop);
        shopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cashRegister.getLocation()));
        if (shop_uuid==null){
            shopBlocksManager.placeShop(newShop, Bukkit.getPlayer(playerUUID), false);
        }
        return true;
    }

    public MethodState deleteShop(UUID player) {
        Shop shop = getPlayerShop(player);
        if (!shop.getItems().isEmpty()) {
            return MethodState.WARNING;
        }
        if (!shopBlocksManager.removeShop(shop)) {
            return MethodState.ESCAPE;
        }
        playerShops.remove(player);
        CompanyManager.shops.remove(shop);
        economyManager.addBalance(player, 400);
        return MethodState.SUCCESS;
    }

    public Shop getPlayerShop(UUID player) {
        return playerShops.get(player);
    }

    public Shop getShopByUUID(UUID uuid) {
        return playerShops.values().stream().filter(shop -> shop.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public boolean hasShop(UUID player) {
        return getPlayerShop(player) != null;
    }

}
