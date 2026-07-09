package fr.openmc.core.features.shops.listener;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import fr.openmc.core.features.shops.manager.ShopManager;
import fr.openmc.core.features.shops.menu.ShopMenu;
import fr.openmc.core.features.shops.menu.ShopSellingMenu;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class ShopListener implements Listener {

    @EventHandler
    public void onShopBreak(BlockBreakEvent e) {
        if (ShopManager.getShopAt(e.getBlock().getLocation()) != null) e.setCancelled(true);
    }

    @EventHandler
    public void onShopExplode(BlockExplodeEvent e) {
        e.blockList().removeIf(block -> ShopManager.getShopAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> ShopManager.getShopAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onShopClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (!block.getType().equals(Material.OAK_SIGN)) return;

        // Check if the clicked block is a sign with tags
        // Instead of getting the entire state of the block
        // This is much faster and avoids unnecessary overhead
        if (!Tag.SIGNS.isTagged(block.getType())) return;
        
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Shop shop = ShopManager.getShopAt(block.getLocation());
        if (shop == null) return;
        
        e.setCancelled(true);
        Player player = e.getPlayer();
        if (shop.isMenuOpened()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.menu.already_opened"), Prefix.SHOP, MessageType.WARNING, true);
            return;
        }
        if (shop.isOwner(player)) {
            if (shop.hasItem()) {
                new ShopMenu(player, shop).open();
                shop.setMenuOpened(true);
            }
            else {
                new ShopSellingMenu(player, shop).open();
                shop.setMenuOpened(true);
            }
        } else {
            if (shop.hasItem()) {
                new ShopMenu(player, shop).open();
                shop.setMenuOpened(true);
            } else {
                MessagesManager.sendMessage(e.getPlayer(), TranslationManager.translation("feature.shop.no_item"), Prefix.SHOP, MessageType.WARNING, true);
            }
        }
    }

    @EventHandler
    public void onInteractWithBlock(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null || block.getType() != Material.BARREL) return;
        
        Shop shop = ShopManager.getShopAt(block.getLocation());
        if (shop == null) return;
        
        if (shop.getOwnerUUID() == null) {
            e.setCancelled(true);
            return;
        }
        
        Player player = e.getPlayer();
        if (!shop.getOwnerUUID().equals(player.getUniqueId())) {
            e.setCancelled(true);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.is_not_him_shop"), Prefix.SHOP, MessageType.WARNING, true);
        }
    }
    
    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent e) {
        CustomFurniture furniture = e.getFurniture();
        
        if (furniture == null || !furniture.getNamespacedID().equals("omc_shops:caisse")) return;
        
        Entity furnitureEntity = furniture.getEntity();
        if (furnitureEntity == null) return;
        if (ShopManager.getShopAt(furnitureEntity.getLocation().toBlockLocation()) == null) return;
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent e) {
		CustomFurniture furniture = e.getFurniture();
        
        if (furniture == null || !furniture.getNamespacedID().equals("omc_shops:caisse")) return;
        
        Player player = e.getPlayer();
        Entity furnitureEntity = furniture.getEntity();
	    if (furnitureEntity == null) {
		    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.entity_is_null"), Prefix.SHOP, MessageType.ERROR, true);
		    return;
	    }
	    
	    Shop shop = ShopManager.getShopAt(furnitureEntity.getLocation().toBlockLocation());
	    if (shop == null) {
		    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.shop_is_null"), Prefix.SHOP, MessageType.ERROR, true);
			return;
	    }
	    
	    e.setCancelled(true);
        if (shop.isMenuOpened()) {
            MessagesManager.sendMessage(e.getPlayer(), TranslationManager.translation("feature.shop.menu.already_opened"), Prefix.SHOP, MessageType.WARNING, true);
            return;
        }
        if (shop.isOwner(player)) {
            if (shop.hasItem()) {
                new ShopMenu(player, shop).open();
                shop.setMenuOpened(true);
            }
            else {
                new ShopSellingMenu(player, shop).open();
                shop.setMenuOpened(true);
            }
        } else {
            if (shop.hasItem()) {
                new ShopMenu(player, shop).open();
                shop.setMenuOpened(true);
            } else {
                MessagesManager.sendMessage(e.getPlayer(), TranslationManager.translation("feature.shop.no_item"), Prefix.SHOP, MessageType.WARNING, true);
            }
        }
    }
    
    @EventHandler
    public void onHopperPickUpItem(InventoryMoveItemEvent e) {
        Inventory source = e.getSource();
        if (source.getLocation() == null) return;
	    if (ShopManager.getShopAt(source.getLocation().toBlockLocation()) == null) return;
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
	    ShopManager.shopBypass.remove(e.getPlayer().getUniqueId());
    }
}
