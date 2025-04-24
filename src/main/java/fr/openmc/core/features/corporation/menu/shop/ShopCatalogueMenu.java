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

import java.util.*;

public class ShopCatalogueMenu extends PaginatedMenu {
    private final Shop shop;
    private final int itemIndex;

    public ShopCatalogueMenu(Player owner, Shop shop, int itemIndex) {
        super(owner);
        this.shop = shop;
        this.itemIndex = itemIndex;
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
        List<ItemStack> items = new ArrayList<>();

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
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        if ((getPage() == 0 && isLastPage()) || shop.getItems().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.setDisplayName("§cRetour"))
                    .setNextMenu(new ShopMenu(getOwner(), shop, itemIndex)));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
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
