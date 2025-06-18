package fr.openmc.core.features.corporation.menu.company;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.api.ItemAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
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

public class ShopManageMenu extends PaginatedMenu {

    private final Company company;

    public ShopManageMenu(Player owner, Company company) {
        super(owner);
        this.company = company;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Shop shop : company.getShops()) {

            List<Component> loc = new ArrayList<>();
            double x = ShopBlocksManager.getMultiblock(shop.getUuid()).getStockBlock().getBlockX();
            double y = ShopBlocksManager.getMultiblock(shop.getUuid()).getStockBlock().getBlockY();
            double z = ShopBlocksManager.getMultiblock(shop.getUuid()).getStockBlock().getBlockZ();

            loc.add(Component.text("§lLocation : §r x : " + x + " y : " + y + " z : " + z));

            items.add(new ItemBuilder(this, Material.BARREL , itemMeta -> {
                itemMeta.setDisplayName("§lshop :§r" + shop.getName());
                itemMeta.lore(loc);
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, CustomItemRegistry.getByName("menu:next_page").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        if ((getPage() == 0 && isLastPage()) || company.getShops().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> itemMeta.setDisplayName("§cRetour"))
                    .setNextMenu(new CompanyMenu(getOwner(), company, false)));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_paginate_company_menu%");
        } else {
            return "Shop Management";
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
}
