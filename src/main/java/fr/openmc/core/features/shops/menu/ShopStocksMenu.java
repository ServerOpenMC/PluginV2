package fr.openmc.core.features.shops.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopItem;
import fr.openmc.core.utils.bukkit.ContainerUtils;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopStocksMenu extends PaginatedMenu {
    
    private final Shop shop;
    private final ShopItem item;
    private final int barrelStocks;
    
    public ShopStocksMenu(Player owner, Shop shop) {
        super(owner);
        this.shop = shop;
        this.barrelStocks = ContainerUtils.getTotalItemsIn((Barrel) this.shop.getMultiblock().stockBlockLoc().getBlock().getState(), this.shop.getItem().getItemStack());
        this.item = shop.getItem();
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
        ShopItem item = this.shop.getItem();
        if (item == null || item.getAmount() <= 0) return List.of();
        return ItemUtils.splitAmountIntoStack(item.getItemStack(), item.getAmount());
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        
        map.put(45, ItemMenuTemplate.BTN_CANCEL.apply(this).setBackButton());
        map.put(48, new ItemMenuBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stocks.fill.name", Component.text(barrelStocks).color(NamedTextColor.GREEN)));
        }).setOnClick(_ -> {
            if (barrelStocks == 0) return;
            ShopItem item = this.shop.getItem();
            int amountToAdd = 28 * item.getItemStack().getMaxStackSize() - item.getAmount();
            if (amountToAdd <= 0) return;
            if (amountToAdd > barrelStocks) amountToAdd = barrelStocks;
            ContainerUtils.removeItemsFromInventory((Barrel) this.shop.getMultiblock().stockBlockLoc().getBlock().getState(), item.getItemStack(), amountToAdd);
            item.addAmount(amountToAdd);
            MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.shop.menu.stocks.fill.success"), Prefix.SHOP, MessageType.SUCCESS, true);
            update();
        }));
        map.put(50, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.COMPANY_BOX.getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stocks.empty.name"));
            itemMeta.lore(List.of(
                    TranslationManager.translation("feature.shop.menu.stocks.empty.lore", Component.text(this.item.getAmount()).color(NamedTextColor.GRAY))
            ));
        }).setOnClick(_ -> {
            if (this.item.getAmount() <= 0) return;
            if (!ItemUtils.hasEnoughSpace(getOwner(), this.item.getItemStack(), this.item.getAmount())) {
                MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.shop.menu.stocks.empty.not_enough_space"), Prefix.SHOP, MessageType.ERROR, true);
                return;
            }
            getOwner().give(getItems());
            this.shop.emptyShop();
            MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.shop.menu.stocks.empty.success"), Prefix.SHOP, MessageType.SUCCESS, true);
            new ShopStocksMenu(getOwner(), shop).open();
        }));
        
        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.shop.menu.stocks.title");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-11::large_shop_menu:";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
