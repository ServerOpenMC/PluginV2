package fr.openmc.core.features.corporation.menu.company;


import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
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

public class CompanySearchMenu extends PaginatedMenu {

    public CompanySearchMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        if (CompanyManager.isInCompany(getOwner().getUniqueId())) {
            return StaticSlots.combine(StaticSlots.getStandardSlots(getInventorySize()), List.of(12, 13, 14));
        }
        return StaticSlots.getStandardSlots(getInventorySize());
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
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Company company : CompanyManager.getCompanies()) {
            ItemStack companyItem;
            if (CompanyManager.isInCompany(getOwner().getUniqueId())) {
                companyItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                    itemMeta.setDisplayName("§e" + company.getName());
                    itemMeta.setLore(List.of(
                            "§7■ Chiffre d'affaires : §a"+ company.getTurnover() + EconomyManager.getEconomyIcon(),
                            "§7■ Marchants : §f" + company.getMerchants().size(),
                            "§7■ Cliquez pour voir les informations de l'enreprise"
                    ));
                }).setOnClick(inventoryClickEvent -> new CompanyMenu(getOwner(), company, true).open());
            } else {
                companyItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                    itemMeta.setDisplayName("§e" + company.getName());
                    itemMeta.setLore(List.of(
                            "§7■ Chiffre d'affaires : §a" + company.getTurnover() + EconomyManager.getEconomyIcon(),
                            "§7■ Marchants : §f" + company.getMerchants().size(),
                            "§7■ Candidatures : §f" + CompanyManager.getPendingApplications(company).size(),
                            "§7■ Cliquez pour postuler"
                    ));
                }).setOnClick((inventoryClickEvent) -> {
                    CompanyManager.applyToCompany(getOwner().getUniqueId(), company);
                    getOwner().sendMessage("§aVous avez postulé pour l'entreprise " + company.getName() + " !");
                    company.broadCastOwner("§a" + getOwner().getName() + " a postulé pour rejoindre l'entreprise !");
                });
            }
            items.add(new ItemBuilder(this, companyItem));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                .setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"))
                .setNextPageButton());
        if (CompanyManager.isInCompany(getOwner().getUniqueId())) {
            map.put(4, new ItemBuilder(this, CompanyManager.getCompany(getOwner().getUniqueId()).getHead(), itemMeta -> {
                itemMeta.setDisplayName("§6§l" + CompanyManager.getCompany(getOwner().getUniqueId()).getName());
                itemMeta.setLore(List.of(
                        "§7■ - Entreprise -",
                        "§7■ Chiffre d'affaires : §a" + CompanyManager.getCompany(getOwner().getUniqueId()).getTurnover() + EconomyManager.getEconomyIcon(),
                        "§7■ Marchants : §f" + CompanyManager.getCompany(getOwner().getUniqueId()).getMerchants().size(),
                        "§7■ Cliquez pour voir les informations de l'entreprise"
                ));
            }).setOnClick(inventoryClickEvent -> new CompanyMenu(getOwner(), CompanyManager.getCompany(getOwner().getUniqueId()), true).open()));
        }
        return map;
    }

    @Override
    public @NotNull String getName() {
        return CompanyManager.isInCompany(getOwner().getUniqueId()) ? "Menu de recherche d'entreprise" : "Pôle travail";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::paginate_company_menu:");
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
