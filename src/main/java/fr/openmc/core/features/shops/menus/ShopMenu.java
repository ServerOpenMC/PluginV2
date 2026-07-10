package fr.openmc.core.features.shops.menus;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.managers.PlayerShopManager;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopItem;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
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
        this.isShopOwner = shop.isOwner(owner);
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
        if (event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) return;
        this.shop.setMenuOpened(false);
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        
        if (this.isShopOwner) {
            map.put(0, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN_RED.getBest(), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.delete.btn.title"));
                itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.delete.btn.lore"));
            }).setOnClick(_ -> new ConfirmMenu(
                    getOwner(),
                    () -> {
                        getOwner().closeInventory();
                        this.shop.setMenuOpened(false);
                        PlayerShopManager.deleteShop(getOwner(), shop);
                    },
                    () -> new ShopMenu(getOwner(), shop).open(),
                    TranslationManager.translationLore("feature.shop.menu.main.delete.confirm.accept"),
                    TranslationManager.translationLore("feature.shop.menu.main.delete.confirm.refuse")
            ).open()));
            
            map.put(1, new ItemMenuBuilder(this, Material.RED_DYE, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove_item.btn.title"));
                itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.remove_item.btn.lore"));
            }).setOnClick(_ -> new ConfirmMenu(
                    getOwner(),
                    () -> {
                        getOwner().closeInventory();
                        this.shop.setMenuOpened(false);
                        if (this.shop.getItem().getAmount() != 0) return;
                        this.shop.removeItem();
                    },
                    () -> new ShopMenu(getOwner(), shop).open(),
                    TranslationManager.translationLore("feature.shop.menu.main.remove_item.confirm.accept"),
                    TranslationManager.translationLore("feature.shop.menu.main.remove_item.confirm.refuse")
            ).open()));
            
            map.put(3, new ItemMenuBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.sells.title"));
                if (this.item == null) itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.stats.lore.error")));
                else itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.sells.lore.success", Component.text(this.shop.getSales().size()).color(NamedTextColor.DARK_PURPLE)));
            }).setOnClick(_ -> {
                if (this.item != null) new ShopSalesMenu(getOwner(), this.shop).open();
            }));
            
            map.put(4, new ItemMenuBuilder(this, Material.GOLD_INGOT, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.stats.title"));
                if (this.item == null) itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.sells.lore.error"));
                else itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.stats.lore.success", Component.text(this.shop.getTurnover() + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD)));
            }).setOnClick(_ -> {
                if (this.item != null) new ShopStatsMenu(getOwner(), this.shop).open();
            }));
            
            map.put(5, new ItemMenuBuilder(this, Material.BARREL, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.stocks.title"));
                if (this.item == null) itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.main.stocks.lore.error")));
                else itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.stocks.lore.success", Component.text(this.shop.getItem().getAmount()).color(NamedTextColor.BLUE)));
            }).setOnClick(_ -> {
                if (this.item != null) new ShopStocksMenu(getOwner(), shop).open();
            }));
            
            map.put(8, new ItemMenuBuilder(this, Material.GREEN_BANNER, itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.yours"))));
            
            map.put(31, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_SHOP, itemMeta ->
                    itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.modify_price.title"))).setOnClick(_ ->
                    DialogInput.send(getOwner(),
                            TranslationManager.translation("feature.shop.menu.selling.price_input"),
                            Integer.MAX_VALUE,
                            s -> {
                                if (!InputUtils.isInputMoney(s)) {
                                    MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.shop.menu.selling.invalid_enter"), Prefix.SHOP, MessageType.SUCCESS, true);
                                    return;
                                }
                                
                                double pricePerItem = InputUtils.convertToMoneyValue(s);
                                if (pricePerItem <= 0) return;
                                
                                shop.getItem().setPricePerItem(pricePerItem);
                                new ShopMenu(getOwner(), shop).open();
                                MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.shop.menu.main.modify_price.message"), Prefix.SHOP, MessageType.SUCCESS, true);
                            })
            ));
        }
		
        map.put(isShopOwner ? 19 : 10, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_64.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove.title", Component.text(64).color(NamedTextColor.RED)));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.remove.lore", Component.text(64).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }).setOnClick(_ -> removeAmount(64)));
        map.put(isShopOwner ? 20 : 11, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_10.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove.title", Component.text(10).color(NamedTextColor.RED)));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.remove.lore", Component.text(10).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }).setOnClick(_ -> removeAmount(10)));
        map.put(isShopOwner ? 21 : 12, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.MINUS_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.remove.title", Component.text(1).color(NamedTextColor.RED)));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.remove.lore", Component.text(1).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }).setOnClick(_ -> removeAmount(1)));
        
        map.put(isShopOwner ? 23 : 14, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.PLUS_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.add.title", Component.text(1).color(NamedTextColor.GREEN)));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.add.lore", Component.text(1).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }).setOnClick(_ -> addAmount(1)));
        map.put(isShopOwner ? 24 : 15, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_10.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.add.title", Component.text(10).color(NamedTextColor.GREEN)));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.add.lore", Component.text(10).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }).setOnClick(_ -> addAmount(10)));
        map.put(isShopOwner ? 25 : 16, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.BTN_64.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.add.title", Component.text(64).color(NamedTextColor.GREEN)));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.add.lore", Component.text(64).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }).setOnClick(_ -> addAmount(64)));
        
        map.put(isShopOwner ? 22 : 13, new ItemMenuBuilder(this, this.item.getItemStack().asOne()));
        
        map.put(isShopOwner ? 30 : 21, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.REFUSE_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.refuse.title"));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.refuse.lore", Component.text(this.amountToBuy).color(NamedTextColor.GOLD)));
        }).setCloseButton());
        map.put(isShopOwner ? 32 : 23, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ACCEPT_BTN.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.accept.title"));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.main.accept.lore",
                    Component.text(this.item.getPrice(this.amountToBuy) + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD),
                    Component.text(this.amountToBuy).color(NamedTextColor.GOLD)));
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
    
    /**
     * Adds the specified amount to the current amount to buy.
     * If the operation is successful, the menu is reopened with the updated amount.
     *
     * @param amount the amount to add to the current amount to buy. Must be greater than zero
     *               and less than or equal to the remaining stock of the item.
     */
    private void addAmount(int amount) {
        if (this.item == null || this.item.getAmount() == 0) return;
        if (amount <= 0) return;
        if ((amountToBuy + amount) > this.item.getAmount()) amountToBuy = this.item.getAmount();
        else this.amountToBuy += amount;
        new ShopMenu(getOwner(), this.shop, this.amountToBuy).open();
    }
    
    /**
     * Reduces the current amount to buy by the specified value while ensuring that the amount
     * remains within valid bounds. If the operation is valid, the menu is reopened with the updated value.
     *
     * @param amount the amount to subtract from the current amount to buy. Must be greater than zero.
     */
    private void removeAmount(int amount) {
        if (this.item == null) return;
        if (amount <= 0) return;
        if (amountToBuy <= amount) amountToBuy = 1;
        else this.amountToBuy -= amount;
        new ShopMenu(getOwner(), this.shop, this.amountToBuy).open();
    }
}
