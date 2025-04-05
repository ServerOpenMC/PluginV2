package fr.openmc.core.features.corporation.menu.shop;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.Shop;
import fr.openmc.core.features.corporation.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ShopCatalogueMenu extends PaginatedMenu {
    private final Shop shop;

    public ShopCatalogueMenu(Player owner, Shop shop) {
        super(owner);
        this.shop = shop;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new java.util.ArrayList<>();

        for (ShopItem shopItem : shop.getItems()){
            items.add(new ItemBuilder(this, shopItem.getItem().getType(), itemMeta -> {

            }).setOnClick(inventoryClickEvent -> {
                new ShopMenu(getOwner(), shop, getIndex(shopItem)).open();
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        return Map.of();
    }

    @Override
    public @NotNull String getName() {
        return shop.getName();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

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
