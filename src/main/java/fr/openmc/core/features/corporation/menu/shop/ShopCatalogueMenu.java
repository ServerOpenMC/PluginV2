package fr.openmc.core.features.corporation.menu.shop;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopItem;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.api.ItemsAdderApi;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCatalogueMenu extends PaginatedMenu {
    private final Shop shop;
    private final int itemIndex;

    public ShopCatalogueMenu(Player owner, Shop shop, int itemIndex) {
        super(owner);
        this.shop = shop;
        this.itemIndex = itemIndex;
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
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (ShopItem shopItem : shop.getItems()){
            items.add(new ItemBuilder(this, shopItem.getItem().getType(), itemMeta -> {
                itemMeta.displayName(ShopItem.getItemName(shopItem.getItem()).color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
            }).setOnClick(inventoryClickEvent -> {
                new ShopMenu(getOwner(), shop, getIndex(shopItem)).open();
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        if ((getPage() == 0 && isLastPage()) || shop.getItems().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§cRetour"))
                    .setNextMenu(new ShopMenu(getOwner(), shop, itemIndex)));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        if (ItemsAdderApi.hasItemAdder()) {
            return "§r§f:offset_-11::large_shop_menu:";
        } else {
            return shop.getName();
        }
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

    /**
     * get the index of a ShopItem
     *
     * @param shopItem the ShopItem
     * @return the index of the ShopItem
     */
    private int getIndex(ShopItem shopItem) {
        int index = 0;
        for (ShopItem items : shop.getItems()){
            if (items==shopItem){
                return index;
            }
            index ++;
        }
        return index;
    }
}
