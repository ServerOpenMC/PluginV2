package fr.openmc.core.features.corporation;

import fr.openmc.core.OMCPlugin;
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
import java.util.Iterator;
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

    public void registerMultiblock(Shop shop, Shop.Multiblock multiblock) {
        multiblocks.put(shop.getUuid(), multiblock);
        Location stockLoc = multiblock.getStockBlock();
        Location cashLoc = multiblock.getCashBlock();
        shopsByLocation.put(stockLoc, shop);
        shopsByLocation.put(cashLoc, shop);
    }

    public Shop.Multiblock getMultiblock(UUID uuid) {
        return multiblocks.get(uuid);
    }

    public Shop getShop(Location location) {
        return shopsByLocation.get(location);
    }

    public void placeShop(Shop shop, Player player, boolean isCompany) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Block stockBlock = multiblock.getStockBlock().getBlock();
        Yaw yaw = WorldUtils.getYaw(player);
        //TODO ItemsAdder cash register
        cashBlock.setType(Material.OAK_SIGN);
        BlockData cashData = cashBlock.getBlockData();
        if (cashData instanceof Directional directional) {
            directional.setFacing(yaw.getOpposite().toBlockFace());
            cashBlock.setBlockData(directional);
        }
    }

    public boolean removeShop(Shop shop) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return false;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Block stockBlock = multiblock.getStockBlock().getBlock();
        //TODO ItemsAdder cash register
        if (cashBlock.getType() != Material.OAK_SIGN || stockBlock.getType() != Material.BARREL) {
            return false;
        }
        multiblocks.remove(shop.getUuid());
        new BukkitRunnable() {
            @Override
            public void run() {
                shopsByLocation.entrySet().removeIf(entry -> entry.getValue().getUuid().equals(shop.getUuid()));
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

}