package fr.openmc.core.features.corporation;

import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.features.economy.EconomyManager;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
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

//    public static void init_db (Connection conn) throws SQLException {
//        conn.prepareStatement("CREATE TABLE IF NOT EXISTS shop (uuid VARCHAR(8) NOT NULL PRIMARY KEY);").executeUpdate();
//    }

    public boolean createShop(Player player, Block barrel, Block cashRegister, UUID shop_uuid) {
        if (!economyManager.withdrawBalance(player.getUniqueId(), 500)) {
            return false;
        }
        Shop newShop;
        if (shop_uuid!=null){
            newShop = new Shop(new ShopOwner(player.getUniqueId()), 0, shop_uuid);
        } else {
            newShop = new Shop(new ShopOwner(player.getUniqueId()), 0);
        }

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
