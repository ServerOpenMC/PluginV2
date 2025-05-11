package fr.openmc.core.features.corporation.listener;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.corporation.menu.shop.ShopMenu;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
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

    private final ShopBlocksManager shopBlocksManager = ShopBlocksManager.getInstance();
    private final Map<UUID, Boolean> inShopBarrel = new HashMap<>();

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        if (shopBlocksManager.getShop(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopExplode(BlockExplodeEvent event){
        event.blockList().removeIf(block -> shopBlocksManager.getShop(block.getLocation()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> shopBlocksManager.getShop(block.getLocation()) != null);
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
            ShopMenu menu = new ShopMenu(event.getPlayer(), shop, 0);
            menu.open();
        }
    }

    @EventHandler
    public void onInteractWithBlock(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block != null && block.getType() == Material.BARREL) {
            Shop shop = shopBlocksManager.getShop(block.getLocation());
            boolean isShop = shop!=null;
            if (isShop){
                Company company = CompanyManager.getInstance().getCompany(e.getPlayer().getUniqueId());
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
            Company company = CompanyManager.getInstance().getCompany(playerUUID);
            if (company!=null){
                if (!company.hasPermission(playerUUID, CorpPermission.SUPPLY)){
                    MessagesManager.sendMessage(player, Component.text("Vous n'avez pas la permission de réapprovisionner les shops dans l'entreprise"), Prefix.SHOP, MessageType.INFO, false);
                    player.closeInventory();
                    return;
                }
            }

            Inventory clickedInventory = e.getClickedInventory();

            if (clickedInventory == null) return;

            if (clickedInventory.getHolder() instanceof Barrel) {
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
            } else if (clickedInventory.getHolder() instanceof Player) {
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
        if (inShopBarrel.getOrDefault(playerUUID, false) && e.getInventory().getHolder() instanceof Barrel) {
            ItemStack item = e.getOldCursor();
            if (isValidItem(item)) {
                removeSupplierKey(item);
            }
        }
    }

    private boolean isValidItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    // Ajoute la clé SUPPLIER_KEY à un item
    private void setSupplierKey(ItemStack item, String uuid) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(OMCPlugin.SUPPLIER_KEY, PersistentDataType.STRING, uuid);
            item.setItemMeta(meta);
        }
    }

    // Retire la clé SUPPLIER_KEY d'un item
    private void removeSupplierKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(OMCPlugin.SUPPLIER_KEY)) {
            meta.getPersistentDataContainer().remove(OMCPlugin.SUPPLIER_KEY);
            item.setItemMeta(meta);
        }
    }
}
