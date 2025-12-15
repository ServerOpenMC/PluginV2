package fr.openmc.core.features.corporation.listener;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import fr.openmc.core.features.corporation.manager.ShopManager;
import fr.openmc.core.features.corporation.menu.ShopMenu;
import fr.openmc.core.features.corporation.models.Shop;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopListener implements Listener {

    private final Map<UUID, Boolean> inShopBarrel = new HashMap<>();

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        if (ShopManager.getShopAt(event.getBlock().getLocation()) != null) event.setCancelled(true);
    }

    @EventHandler
    public void onShopExplode(BlockExplodeEvent event){
        event.blockList().removeIf(block -> ShopManager.getShopAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> ShopManager.getShopAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onShopClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        // Check if the clicked block is a sign with tags
        // Instead of getting the entire state of the block
        // This is much faster and avoids unnecessary overhead
        if (!Tag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
        
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Shop shop = ShopManager.getShopAt(event.getClickedBlock().getLocation());
        if (shop == null) return;
        
        event.setCancelled(true);
        ShopMenu menu = new ShopMenu(event.getPlayer());
        menu.open();
    }

    @EventHandler
    public void onInteractWithBlock(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getType() != Material.BARREL) return;
        
        Shop shop = ShopManager.getShopAt(block.getLocation());
        if (shop == null) return;
        
        if (shop.getOwnerUUID() == null) {
            e.setCancelled(true);
            return;
        }
        if (!shop.getOwnerUUID().equals(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    /**
     * check if an item is valid
     *
     * @param item the item to check
     * @return true if it's a valid item
     */
    private boolean isValidItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }
    
    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        CustomFurniture furniture = event.getFurniture();
        
        if (furniture != null && furniture.getNamespacedID().equals("omc_company:caisse")) event.setCancelled(true);
    }
    
    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent e) {
		CustomFurniture furniture = e.getFurniture();
		
        if (furniture == null) return;
        
        if (!furniture.getNamespacedID().equals("omc_company:caisse")) return;
	    
	    if (furniture.getEntity() == null) {
		    MessagesManager.sendMessage(e.getPlayer(), Component.text("§cErreur lors de l'ouverture du shop, veuillez contacter le staff. (Entity is null)"), Prefix.SHOP, MessageType.ERROR, true);
		    return;
	    }
	    
	    Shop shop = ShopManager.getShopAt(furniture.getEntity().getLocation().subtract(0, 1, 0).toBlockLocation());
	    if (shop == null) {
		    MessagesManager.sendMessage(e.getPlayer(), Component.text("§cErreur lors de l'ouverture du shop, veuillez contacter le staff. (Shop is null)"), Prefix.SHOP, MessageType.ERROR, true);
			return;
	    }
	    
	    e.setCancelled(true);
	    new ShopMenu(e.getPlayer()).open();
    }
}
