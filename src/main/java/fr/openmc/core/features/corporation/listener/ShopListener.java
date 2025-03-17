package fr.openmc.core.features.corporation.listener;

import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.PlayerShopManager;
import fr.openmc.core.features.corporation.Shop;
import fr.openmc.core.features.corporation.ShopBlocksManager;
import fr.openmc.core.features.corporation.menu.shop.ShopMenu;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShopListener implements Listener {

    private final CompanyManager companyManager = CompanyManager.getInstance();
    private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();
    private final ShopBlocksManager shopBlocksManager = ShopBlocksManager.getInstance();

    //TODO ItemsAdder caisse

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        if (shopBlocksManager.getShop(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getState() instanceof Sign) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Shop shop = shopBlocksManager.getShop(event.getClickedBlock().getLocation());
            if (shop == null) {
                return;
            }
            event.setCancelled(true);
            ShopMenu menu = new ShopMenu(event.getPlayer(), companyManager, playerShopManager, shop, 0);
            menu.open();
        }
    }
}
