package fr.openmc.core.features.corporation.menu.company;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.economy.EconomyManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CompanyBaltopMenu extends Menu {

    public CompanyBaltopMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Baltop des entreprises";
    }

    @Override
    public String getTexture() {
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_company_baltop_menu%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        List<Company> companies = CompanyManager.companies;
        companies.sort((company1, company2) -> Double.compare(company2.getTurnover(), company1.getTurnover()));
        Map<Integer, ItemBuilder> content = new HashMap<>();
        content.put(37, new ItemBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName("§6§l" + "Baltop des entreprises");
            itemMeta.lore(List.of(
                    Component.text("§7■ Voici les 3 entreprises les plus riches du serveur"),
                    Component.text("§7■ Les entreprises sont classées en fonction de leur chiffre d'affaires")
            ));
        }));
        content.put(41, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName("§cFermer")).setCloseButton());
        if (companies.isEmpty()) return content;
        content.put(10, new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§61. §e" + companies.getFirst().getName());
            itemMeta.lore(List.of(
                    Component.text("§7■ Chiffre d'affaire : §a" + companies.getFirst().getTurnover() + EconomyManager.getEconomyIcon()),
                            Component.text("§7■ Marchants : §a" + companies.getFirst().getMerchants().size())
            ));
        }));
        UUID ownerUUIDFirst;
        if (companies.getFirst().getOwner().isCity()) ownerUUIDFirst = companies.getFirst().getOwner().getCity().getPlayerWithPermission(CPermission.OWNER);
        else ownerUUIDFirst = companies.getFirst().getOwner().getPlayer();
        content.put(12, new ItemBuilder(this, companies.getFirst().getHead(), itemMeta -> {
            itemMeta.setDisplayName("§6" + (companies.getFirst().getOwner().isCity() ? companies.getFirst().getOwner().getCity().getName() : Bukkit.getOfflinePlayer(ownerUUIDFirst).getName()));
            itemMeta.lore(List.of(
                    Component.text("§4■ Propriétaire")
            ));
        }));
        for (int i = 13; i <= 16; i++) {
            ItemStack merchantHead;
            UUID merchantUUID;
            if (companies.getFirst().getMerchantsUUID().size() <= i - 13) {
                merchantUUID = null;
                merchantHead = new ItemStack(Material.AIR);
            } else {
                merchantUUID = companies.getFirst().getMerchantsUUID().get(i - 13);
                if (merchantUUID == null) merchantHead = new ItemStack(Material.AIR);
                else merchantHead = ItemUtils.getPlayerSkull(merchantUUID);
            }
            content.put(i, new ItemBuilder(this, merchantHead, itemMeta -> {
                if (merchantUUID == null) return;
                itemMeta.setDisplayName("§8" + Bukkit.getOfflinePlayer(merchantUUID).getName());
                MerchantData merchantData = companies.getFirst().getMerchants().get(merchantUUID);
                itemMeta.lore(List.of(
                        Component.text("§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items"),
                        Component.text("§7■ A gagné §a" + merchantData.getMoneyWon() + EconomyManager.getEconomyIcon())
                ));
            }));
        }
        if (companies.size() == 1) return content;
        content.put(19, new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§62. §e" + companies.get(1).getName());
            itemMeta.lore(List.of(
                    Component.text("§7■ Chiffre d'affaire : §a" + companies.get(1).getTurnover() + EconomyManager.getEconomyIcon()),
                    Component.text("§7■ Marchants : §a" + companies.get(1).getMerchants().size())
            ));
        }));
        UUID ownerUUIDSecond;
        if (companies.get(1).getOwner().isCity()) ownerUUIDSecond = companies.get(1).getOwner().getCity().getPlayerWithPermission(CPermission.OWNER);
        else ownerUUIDSecond = companies.get(1).getOwner().getPlayer();
        content.put(21, new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUIDSecond), itemMeta -> {
            itemMeta.setDisplayName("§6" + (companies.get(1).getOwner().isCity() ? companies.get(1).getName() : Bukkit.getOfflinePlayer(ownerUUIDSecond).getName()));
            itemMeta.lore(List.of(
                    Component.text("§4■ Propriétaire")
            ));
        }));
        for (int i = 22; i <= 25; i++) {
            ItemStack merchantHead;
            UUID merchantUUID;
            if (companies.get(1).getMerchantsUUID().size() <= i - 22) {
                merchantUUID = null;
                merchantHead = new ItemStack(Material.AIR);
            }
            else {
                merchantUUID = companies.get(1).getMerchantsUUID().get(i - 22);
                if (merchantUUID == null) merchantHead = new ItemStack(Material.AIR);
                else merchantHead = ItemUtils.getPlayerSkull(merchantUUID);
            }
            content.put(i, new ItemBuilder(this, merchantHead, itemMeta -> {
                if (merchantUUID == null) return;
                itemMeta.setDisplayName("§8" + Bukkit.getOfflinePlayer(merchantUUID).getName());
                MerchantData merchantData = companies.get(1).getMerchants().get(merchantUUID);
                itemMeta.lore(List.of(
                        Component.text("§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items"),
                        Component.text("§7■ A gagné §a" + merchantData.getMoneyWon() + EconomyManager.getEconomyIcon())
                ));
            }));
        }
        if (companies.size() == 2) return content;
        content.put(28, new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§63. §e"+ companies.get(2).getName());
            itemMeta.lore(List.of(
                    Component.text("§7■ Chiffre d'affaire : §a" + companies.get(2).getTurnover() + EconomyManager.getEconomyIcon()),
                    Component.text("§7■ Marchants : §a" + companies.get(2).getMerchants().size())
            ));
        }));
        UUID ownerUUIDThird;
        if (companies.get(2).getOwner().isCity()) ownerUUIDThird = companies.get(2).getOwner().getCity().getPlayerWithPermission(CPermission.OWNER);
        else ownerUUIDThird = companies.get(2).getOwner().getPlayer();
        content.put(30, new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUIDThird), itemMeta -> {
            itemMeta.setDisplayName("§6" + (companies.get(2).getOwner().isCity() ? companies.get(2).getName() : Bukkit.getOfflinePlayer(ownerUUIDThird).getName()));
            itemMeta.lore(List.of(
                    Component.text("§4■ Propriétaire")
            ));
        }));
        for (int i = 31; i <= 34; i++) {
            ItemStack merchantHead;
            UUID merchantUUID;
            if (companies.get(2).getMerchantsUUID().size() <= i - 31) {
                merchantUUID = null;
                merchantHead = new ItemStack(Material.AIR);
            }
            else {
                merchantUUID = companies.get(2).getMerchantsUUID().get(i - 31);
                if (merchantUUID == null) merchantHead = new ItemStack(Material.AIR);
                else merchantHead = ItemUtils.getPlayerSkull(merchantUUID);
            }
            content.put(i, new ItemBuilder(this, merchantHead, itemMeta -> {
                if (merchantUUID == null) return;
                itemMeta.setDisplayName("§8" + Bukkit.getOfflinePlayer(merchantUUID).getName());
                MerchantData merchantData = companies.get(2).getMerchants().get(merchantUUID);
                itemMeta.lore(List.of(
                        Component.text("§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items"),
                        Component.text("§7■ A gagné §a" + merchantData.getMoneyWon() + EconomyManager.getEconomyIcon())
                ));
            }));
        }
        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
