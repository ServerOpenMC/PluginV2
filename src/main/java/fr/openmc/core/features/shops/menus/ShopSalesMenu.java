package fr.openmc.core.features.shops.menus;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopSale;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopSalesMenu extends PaginatedMenu {

    private final Shop shop;
    
    public ShopSalesMenu(Player owner, Shop shop) {
        super(owner);
        this.shop = shop;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        List<ShopSale> sales = this.shop.getSales();
        sales.forEach(s -> {
            ItemStack item = s.getItem().getItemStack().clone();
            item.editMeta(itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.shop.menu.sales.item.name", PlayerNameCache.name(s.getBuyerUUID()).color(NamedTextColor.LIGHT_PURPLE)));
                itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.sales.item.lore",
                                Component.text(s.getDate().toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))).color(NamedTextColor.GREEN),
                                Component.text(s.getAmount()).color(NamedTextColor.GOLD),
                                Component.text(s.getPrice() + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
            });
            items.add(item);
        });
        return items;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(45, ItemMenuTemplate.BTN_CANCEL.apply(this).setBackButton());
        map.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        map.put(49, new ItemMenuBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            Component lastRemoval = this.shop.getLastWithdrawal() == null ? TranslationManager.translation("global.never") : Component.text(this.shop.getLastWithdrawal().toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.sales.get_turnover.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.shop.menu.sales.get_turnover.lore", Component.text(this.shop.getTurnover() * 0.8 + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD), lastRemoval.color(NamedTextColor.LIGHT_PURPLE)));
        }).setOnClick(_ -> {
            this.shop.withdrawTurnover();
            this.shop.setLastWithdrawalToNow();
            update();
        }));
        map.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));
        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.shop.menu.sales.title");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-11::large_shop_menu:";
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        this.shop.setMenuOpened(false);
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
