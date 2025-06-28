package fr.openmc.core.features.corporation.listener;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.corporation.menu.shop.ShopMenu;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomItemsCompanyListener implements Listener {

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event){
        CustomFurniture furniture = event.getFurniture();

        if (furniture!=null && furniture.getNamespacedID().equals("omc_company:caisse") && !event.getPlayer().isOp()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent e){
        if (e.getFurniture() == null) {
            return;
        }

        if (e.getFurniture().getNamespacedID().equals("omc_company:caisse")){

            double x = e.getFurniture().getEntity().getLocation().getBlockX();
            double y = e.getFurniture().getEntity().getLocation().getBlockY();
            double z = e.getFurniture().getEntity().getLocation().getBlockZ();

            Shop shop = ShopBlocksManager.getShop(new Location(e.getFurniture().getEntity().getWorld(), x, y, z));
            if (shop == null) {
                return;
            }
            e.setCancelled(true);
            ShopMenu menu = new ShopMenu(e.getPlayer(), shop, 0);
            menu.open();
        }
    }
}
