package fr.openmc.core.disabled.corporation.listener;

import fr.openmc.core.disabled.corporation.CorpPermission;
import fr.openmc.core.disabled.corporation.company.Company;
import fr.openmc.core.disabled.corporation.manager.CompanyManager;
import fr.openmc.core.disabled.corporation.manager.ShopBlocksManager;
import fr.openmc.core.disabled.corporation.menu.shop.ShopMenu;
import fr.openmc.core.disabled.corporation.shops.Shop;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopListener implements Listener {

    private final Map<UUID, Boolean> inShopBarrel = new HashMap<>();

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        if (ShopBlocksManager.getShop(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopExplode(BlockExplodeEvent event){
        event.blockList().removeIf(block -> ShopBlocksManager.getShop(block.getLocation()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> ShopBlocksManager.getShop(block.getLocation()) != null);
    }

    @EventHandler
    public void onShopClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        // Check if the clicked block is a sign with tags
        // Instead of getting the entire state of the block,
        // This is much faster and avoids unnecessary overhead
        if (!Tag.SIGNS.isTagged(event.getClickedBlock().getType()))
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Shop shop = ShopBlocksManager.getShop(event.getClickedBlock().getLocation());
        if (shop == null)
            return;
        
        event.setCancelled(true);
        ShopMenu menu = new ShopMenu(event.getPlayer(), shop, 0);
        menu.open();
    }

    @EventHandler
    public void onInteractWithBlock(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block != null && block.getType() == Material.BARREL) {
            Shop shop = ShopBlocksManager.getShop(block.getLocation());
            boolean isShop = shop!=null;
            if (isShop){
                Company company = CompanyManager.getCompany(e.getPlayer().getUniqueId());
                if (company==null){
                    if (shop.getOwner().getPlayer()==null){
                        e.setCancelled(true);
                        return;
                    }
                    if (!shop.getOwner().getPlayer().equals(e.getPlayer().getUniqueId())){
                        e.setCancelled(true);
                        return;
                    }
                } else {
                    if (!company.hasShop(shop.getUuid())){
                        e.setCancelled(true);
                        MessagesManager.sendMessage(e.getPlayer(), Component.text("Tu n'es pas dans l'entrprise possédant ce shop"), Prefix.SHOP, MessageType.INFO, false);
                        return;
                    }

                    if (!company.hasPermission(e.getPlayer().getUniqueId(), CorpPermission.SUPPLY)){
                        e.setCancelled(true);
                        MessagesManager.sendMessage(e.getPlayer(), Component.text("Tu n'as pas la permission de réapprovisionner le shop"), Prefix.SHOP, MessageType.INFO, false);
                        return;
                    }
                }
            }
            inShopBarrel.put(e.getPlayer().getUniqueId(), isShop);
        }
    }

    @EventHandler
    public void onShopPutItem(InventoryClickEvent e) {
        UUID playerUUID = e.getWhoClicked().getUniqueId();
        if (inShopBarrel.getOrDefault(playerUUID, false)) {
            Player player = (Player) e.getWhoClicked();
            Company company = CompanyManager.getCompany(playerUUID);
            if (company!=null){
                if (!company.hasPermission(playerUUID, CorpPermission.SUPPLY)){
                    MessagesManager.sendMessage(player, Component.text("Vous n'avez pas la permission de réapprovisionner les shops dans l'entreprise"), Prefix.SHOP, MessageType.INFO, false);
                    player.closeInventory();
                    return;
                }
            }

            Inventory clickedInventory = e.getClickedInventory();

            if (clickedInventory == null) return;

            if (clickedInventory.getHolder(false) instanceof Barrel) {
                ItemStack currentItem = e.getCurrentItem();
                ItemStack cursorItem = e.getCursor();

                if (e.isShiftClick() && isValidItem(currentItem)) {
                    removeSupplierKey(currentItem);
                }
                // Vérifier si un item est retiré
                else if (e.getAction().name().contains("PICKUP") && isValidItem(currentItem)) {
                    removeSupplierKey(currentItem);
                }
                else if (e.getAction().name().contains("SWAP") && isValidItem(currentItem)) {
                    removeSupplierKey(currentItem);
                }
                // Vérifier si un item est placé avec la souris
                else if (e.getAction().name().contains("PLACE") && isValidItem(cursorItem)) {
                    setSupplierKey(cursorItem, player.getUniqueId().toString());
                }
            } else if (clickedInventory.getHolder(false) instanceof Player) {
                ItemStack currentItem = e.getCurrentItem();

                if (e.isShiftClick() && !e.getAction().name().contains("SWAP") && isValidItem(currentItem)) {
                    setSupplierKey(currentItem, player.getUniqueId().toString());
                }
            }
        }
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent e) {
        UUID playerUUID = e.getWhoClicked().getUniqueId();
        if (inShopBarrel.getOrDefault(playerUUID, false) && e.getInventory().getHolder(false) instanceof Barrel) {
            ItemStack item = e.getOldCursor();
            if (isValidItem(item)) {
                removeSupplierKey(item);
            }
        }
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

    /**
     * add the SUPPLIER_KEY to an item
     *
     * @param item the item to add the key
     * @param uuid the uuid of the player (the supplier_key)
     */
    private void setSupplierKey(ItemStack item, String uuid) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(CompanyManager.SUPPLIER_KEY, PersistentDataType.STRING, uuid);
            item.setItemMeta(meta);
        }
    }

    /**
     * remove the SUPPLIER_KEY to an item
     *
     * @param item the item to remove the key
     */
    private void removeSupplierKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(CompanyManager.SUPPLIER_KEY)) {
            meta.getPersistentDataContainer().remove(CompanyManager.SUPPLIER_KEY);
            item.setItemMeta(meta);
        }
    }
}
