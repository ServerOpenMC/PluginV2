package fr.openmc.core.features.corporation;

import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.features.economy.EconomyManager;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerShopManager {

    private final Map<UUID, Shop> playerShops = new HashMap<>();
    private final EconomyManager economyManager;
    private final ShopBlocksManager shopBlocksManager;

    public PlayerShopManager(EconomyManager economyManager, ShopBlocksManager shopBlocksManager) {
        this.economyManager = economyManager;
        this.shopBlocksManager = shopBlocksManager;
    }

    public boolean createShop(Player player, Block barrel, Block cashRegister) {
        if (!economyManager.withdrawBalance(player.getUniqueId(), 500)) {
            return false;
        }
        Shop newShop = new Shop(new ShopOwner(player.getUniqueId()), 0, economyManager, shopBlocksManager);
        playerShops.put(player.getUniqueId(), newShop);
        shopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cashRegister.getLocation()));
        shopBlocksManager.placeShop(newShop, player, false);
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
