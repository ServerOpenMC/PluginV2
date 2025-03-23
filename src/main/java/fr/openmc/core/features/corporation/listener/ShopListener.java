package fr.openmc.core.features.corporation.listener;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.PlayerShopManager;
import fr.openmc.core.features.corporation.Shop;
import fr.openmc.core.features.corporation.ShopBlocksManager;
import fr.openmc.core.features.corporation.menu.shop.ShopMenu;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopListener implements Listener {

    private final CompanyManager companyManager = CompanyManager.getInstance();
    private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();
    private final ShopBlocksManager shopBlocksManager = ShopBlocksManager.getInstance();
    private final Map<UUID, Boolean> inShopBarrel = new HashMap<>();

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

    @EventHandler
    public void onInteractWithBlock (PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block != null){
            if (block.getType() == Material.BARREL) {
                boolean isShop = shopBlocksManager.getShop(e.getClickedBlock().getLocation()) != null;
                OMCPlugin.getInstance().getLogger().info("" + isShop);
                if (inShopBarrel.containsKey(e.getPlayer().getUniqueId())){
                    inShopBarrel.replace(e.getPlayer().getUniqueId(), isShop);
                } else {
                    inShopBarrel.put(e.getPlayer().getUniqueId(), isShop);
                }
            }
        }
    }

    @EventHandler
    public void onShopPutItem (InventoryClickEvent e) {
        if (inShopBarrel.containsKey(e.getWhoClicked().getUniqueId())){
            OMCPlugin.getInstance().getLogger().info("test");
            if (inShopBarrel.get(e.getWhoClicked().getUniqueId())){
                OMCPlugin.getInstance().getLogger().info("test 1");
                Player player = (Player) e.getWhoClicked();
                Inventory clickedInventory = e.getClickedInventory();

                if (clickedInventory == null) return;

                InventoryHolder holder = clickedInventory.getHolder();

                if (holder instanceof Barrel) {
                    ItemStack currentItem = e.getCurrentItem();
                    ItemStack cursorItem = e.getCursor();

                    if (e.isShiftClick()) {
                        if (currentItem != null && currentItem.getType() != Material.AIR) {
                            if (!currentItem.getItemMeta().getPersistentDataContainer().has(OMCPlugin.SUPPLIER_KEY)) {
                                ItemMeta itemMeta = currentItem.getItemMeta();
                                itemMeta.getPersistentDataContainer().set(OMCPlugin.SUPPLIER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
                                currentItem.setItemMeta(itemMeta);
                                OMCPlugin.getInstance().getLogger().info("itemMeta set");
                            }
                        }
                    } else if (e.getAction().name().contains("PICKUP")) {
                        if (currentItem != null && currentItem.getType() != Material.AIR) {
                            if (!currentItem.getItemMeta().getPersistentDataContainer().has(OMCPlugin.SUPPLIER_KEY)) {
                                ItemMeta itemMeta = currentItem.getItemMeta();
                                currentItem.getItemMeta().getPersistentDataContainer().remove(OMCPlugin.SUPPLIER_KEY);
                                currentItem.setItemMeta(itemMeta);
                                OMCPlugin.getInstance().getLogger().info("itemMeta unset");
                            }
                        }
                    } else if (e.getAction().name().contains("PLACE")) {

                        if (cursorItem.getType() != Material.AIR) {
                            if (!cursorItem.getItemMeta().getPersistentDataContainer().has(OMCPlugin.SUPPLIER_KEY)) {
                                ItemMeta itemMeta =cursorItem.getItemMeta();
                                cursorItem.getItemMeta().getPersistentDataContainer().set(OMCPlugin.SUPPLIER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
                                cursorItem.setItemMeta(itemMeta);
                                OMCPlugin.getInstance().getLogger().info("itemMeta set");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent e) {
        if (inShopBarrel.containsKey(e.getWhoClicked().getUniqueId())){
            if (inShopBarrel.get(e.getWhoClicked().getUniqueId())) {
                if (e.getInventory().getHolder() instanceof Barrel) {
                    ItemStack item = e.getOldCursor();
                    if (item.getItemMeta().getPersistentDataContainer().has(OMCPlugin.SUPPLIER_KEY)){
                        item.getItemMeta().getPersistentDataContainer().remove(OMCPlugin.SUPPLIER_KEY);
                    }
                }
            }
        }
    }
}
