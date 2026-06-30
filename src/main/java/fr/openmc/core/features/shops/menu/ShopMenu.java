package fr.openmc.core.features.shops.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.manager.PlayerShopManager;
import fr.openmc.core.features.shops.manager.ShopManager;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopItem;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMenu extends Menu {

    private int amountToBuy;
    private final Shop shop;
    private final ShopItem item;
    private final boolean isShopOwner;
    
    private final InventorySize size;
    private final String texture;
    
    public ShopMenu(Player owner, Shop shop) {
        this(owner, shop, 1);
    }
    
    public ShopMenu(Player owner, Shop shop, int amountToBuy) {
        super(owner);
        this.shop = shop;
        this.item = shop.getItem();
        this.isShopOwner = ShopManager.isShopOwner(owner, shop);
        this.size = isShopOwner ? InventorySize.LARGER : InventorySize.LARGE;
        this.texture = isShopOwner ? "shop_menu" : "sell_shop_menu";
        this.amountToBuy = amountToBuy;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.shop.menu.main.title", PlayerNameCache.name(getOwner().getUniqueId()));
    }

    @Override
    public String getTexture() {
		return "§r§f:offset_-11::" + this.texture + ":";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return this.size;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        this.shop.setMenuOpened(false);
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        
        if (this.isShopOwner) {
            map.put(0, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN_RED.getBest(), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.delete.btn.title"));
                itemMeta.lore(List.of(
                        TranslationManager.translation("feature.shop.menu.main.delete.btn.lore1"),
                        TranslationManager.translation("feature.shop.menu.main.delete.btn.lore2")
                ));
            }).setOnClick(_ -> new ConfirmMenu(
                    getOwner(),
                    () -> {
                        getOwner().closeInventory();
                        PlayerShopManager.deleteShop(getOwner(), shop);
                    },
                    () -> new ShopMenu(getOwner(), shop).open(),
                    List.of(TranslationManager.translation("feature.shop.menu.main.delete.confirm.accept")),
                    List.of(TranslationManager.translation("feature.shop.menu.main.delete.confirm.refuse"))
            ).open()));
            
            map.put(1, new ItemMenuBuilder(this, Material.RED_DYE, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove_item.btn.title"));
                itemMeta.lore(List.of(
                        TranslationManager.translation("feature.shop.menu.main.remove_item.btn.lore1"),
                        TranslationManager.translation("feature.shop.menu.main.remove_item.btn.lore2")
                ));
            }).setOnClick(_ -> new ConfirmMenu(
                    getOwner(),
                    () -> {
                        getOwner().closeInventory();
                        if (this.shop.getItem().getAmount() != 0) return;
                        this.shop.removeItem();
                    },
                    () -> new ShopMenu(getOwner(), shop).open(),
                    List.of(TranslationManager.translation("feature.shop.menu.main.remove_item.confirm.accept")),
                    List.of(TranslationManager.translation("feature.shop.menu.main.remove_item.confirm.refuse"))
            ).open()));
            
            map.put(3, new ItemMenuBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.sells.title"));
                if (this.item == null) itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.stats.lore.error")));
                else itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.sells.lore.success", Component.text(this.shop.getSales().size()).color(NamedTextColor.DARK_PURPLE))));
            }).setOnClick(_ -> {
                if (this.item != null) new ShopSalesMenu(getOwner(), this.shop).open();
            }));
            
            map.put(4, new ItemMenuBuilder(this, Material.GOLD_INGOT, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.stats.title"));
                if (this.item == null) itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.sells.lore.error")));
                else itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.stats.lore.success", Component.text(this.shop.getTurnover() + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD))));
            }).setOnClick(_ -> {
                if (this.item != null) new ShopStatsMenu(getOwner(), this.shop).open();
            }));
            
            map.put(5, new ItemMenuBuilder(this, Material.BARREL, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.stocks.title"));
                if (this.item == null) itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.stocks.lore.error")));
                else itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.stocks.lore.success", Component.text(this.shop.getItem().getAmount()).color(NamedTextColor.BLUE))));
            }).setOnClick(_ -> {
                if (this.item != null) new ShopStocksMenu(getOwner(), shop).open();
            }));
            
            map.put(8, new ItemMenuBuilder(this, Material.GREEN_BANNER, itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.yours"))));
        }
		
        map.put(isShopOwner ? 19 : 10, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_64.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove.title", Component.text(64).color(NamedTextColor.RED)));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.remove.lore", Component.text(64).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            ));
        }).setOnClick(_ -> removeAmount(64)));
        map.put(isShopOwner ? 20 : 11, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_10.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove.title", Component.text(10).color(NamedTextColor.RED)));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.remove.lore", Component.text(10).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            ));
        }).setOnClick(_ -> removeAmount(10)));
        map.put(isShopOwner ? 21 : 12, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.MINUS_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove.title", Component.text(1).color(NamedTextColor.RED)));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.remove.lore", Component.text(1).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            ));
        }).setOnClick(_ -> removeAmount(1)));
        
        map.put(isShopOwner ? 23 : 14, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.PLUS_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.add.title", Component.text(1).color(NamedTextColor.GREEN)));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.add.lore", Component.text(1).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            ));
        }).setOnClick(_ -> addAmount(1)));
        map.put(isShopOwner ? 24 : 15, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_10.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.add.title", Component.text(10).color(NamedTextColor.GREEN)));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.add.lore", Component.text(10).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            ));
        }).setOnClick(_ -> addAmount(10)));
        map.put(isShopOwner ? 25 : 16, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_64.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.add.title", Component.text(64).color(NamedTextColor.GREEN)));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.add.lore", Component.text(64).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            ));
        }).setOnClick(_ -> addAmount(64)));
        
        map.put(22, new ItemMenuBuilder(this, this.item.getItemStack().asOne()));
        
        map.put(isShopOwner ? 30 : 21, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.REFUSE_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.refuse.title"));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.refuse.lore1", Component.text(this.amountToBuy).color(NamedTextColor.GOLD)),
                    TranslationManager.translation("feature.shop.menu.main.refuse.lore2")
            ));
        }).setCloseButton());
        map.put(isShopOwner ? 32 : 23, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ACCEPT_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.accept.title"));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.main.accept.lore1", Component.text(this.item.getPrice(this.amountToBuy) + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD), Component.text(this.amountToBuy).color(NamedTextColor.GOLD)),
                    TranslationManager.translation("feature.shop.menu.main.accept.lore2")
            ));
        }).setOnClick(_ -> {
            this.shop.buy(getOwner(), this.amountToBuy);
            getOwner().closeInventory();
        }));
        
        return map;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
    
    private void addAmount(int amount) {
        if (this.item == null || this.item.getAmount() == 0) return;
        if (amount <= 0) return;
        if ((amountToBuy + amount) > this.item.getAmount()) amountToBuy = this.item.getAmount();
        else this.amountToBuy += amount;
        new ShopMenu(getOwner(), this.shop, this.amountToBuy).open();
    }
    
    private void removeAmount(int amount) {
        if (this.item == null) return;
        if (amount <= 0) return;
        if (amountToBuy <= amount) amountToBuy = 1;
        else this.amountToBuy -= amount;
        new ShopMenu(getOwner(), this.shop, this.amountToBuy).open();
    }
}
