package fr.openmc.core.features.corporation.menu.company;


import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.data.TransactionData;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.api.ItemAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyBankTransactionsMenu extends PaginatedMenu {

    private final Company company;

    public CompanyBankTransactionsMenu(Player owner, Company company) {
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
        List<Long> timestamps = new ArrayList<>(company.getTransactions().getQueue().keySet());
        List<TransactionData> transactions = new ArrayList<>(company.getTransactions().getQueue().values());
        for (int i = 0; i < timestamps.size(); i++) {
            long timestamp = timestamps.get(i);
            TransactionData transaction = transactions.get(i);
            int finalI = i;
            items.add(new ItemBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.setDisplayName("§eTransaction #" + finalI);
                List<String> lore = new ArrayList<>(List.of(
                        "§7■ Date: §f" + new SimpleDateFormat("MM/dd/yyyy").format(timestamp),
                        "§7■ Nature: §f" + transaction.nature(),
                        "§7■ Par: §f" + Bukkit.getOfflinePlayer(transaction.sender()).getName()
                ));
                if (transaction.place() != null && !transaction.place().isEmpty()) {
                    lore.add("§7■ Lieu: §f" + transaction.place());
                }
                lore.add("§7■ Montant: " + EconomyManager.getFormattedNumber(transaction.value()));
                itemMeta.setLore(lore);
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
            return "Transactions de l'entreprise";
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
