package fr.openmc.core.features.corporation.menu.company;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.corporation.Company;
import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.data.MerchantData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        List<Company> companies = CompanyManager.companies;
        companies.sort((company1, company2) -> Double.compare(company2.getTurnover(), company1.getTurnover()));
        Map<Integer, ItemStack> content = fill(Material.GRAY_STAINED_GLASS_PANE);
        content.put(46, new ItemBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName("§6§l" + "Baltop des entreprises");
            itemMeta.setLore(List.of(
                    "§7■ Voici les 3 entreprises les plus riches du serveur",
                    "§7■ Les entreprises sont classées en fonction de leur chiffre d'affaires"
            ));
        }));
        content.put(50, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName("§cFermer")).setCloseButton());
        if (companies.isEmpty()) return content;
        content.put(10, new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§61. §e" + companies.getFirst().getName());
            itemMeta.setLore(List.of(
                    "§7■ Chiffre d'affaires : §a" + companies.getFirst().getTurnover() + "€",
                    "§7■ Marchants : §a" + companies.getFirst().getMerchants().size()
            ));
        }));
        UUID ownerUUIDFirst;
        if (companies.getFirst().getOwner().isTeam()) ownerUUIDFirst = companies.getFirst().getOwner().getCity().getPlayerWith(CPermission.OWNER);
        else ownerUUIDFirst = companies.getFirst().getOwner().getPlayer();
        content.put(12, new ItemBuilder(this, companies.getFirst().getHead(), itemMeta -> {
            itemMeta.setDisplayName("§6" + (companies.getFirst().getOwner().isTeam() ? companies.getFirst().getOwner().getCity().getName() : Bukkit.getOfflinePlayer(ownerUUIDFirst).getName()));
            itemMeta.setLore(List.of(
                    "§4■ Propriétaire"
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
                itemMeta.setLore(List.of(
                        "§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items",
                        "§7■ A gagné §a" + merchantData.getMoneyWon() + "€"
                ));
            }));
        }
        if (companies.size() == 1) return content;
        content.put(19, new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§62. §e" + companies.get(1).getName());
            itemMeta.setLore(List.of(
                    "§7■ Chiffre d'affaires : §a" + companies.get(1).getTurnover() + "€",
                    "§7■ Marchants : §a" + companies.get(1).getMerchants().size()
            ));
        }));
        UUID ownerUUIDSecond;
        if (companies.get(1).getOwner().isTeam()) ownerUUIDSecond = companies.get(1).getOwner().getCity().getPlayerWith(CPermission.OWNER);
        else ownerUUIDSecond = companies.get(1).getOwner().getPlayer();
        content.put(21, new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUIDSecond), itemMeta -> {
            itemMeta.setDisplayName("§6" + (companies.get(1).getOwner().isTeam() ? companies.get(1).getName() : Bukkit.getOfflinePlayer(ownerUUIDSecond).getName()));
            itemMeta.setLore(List.of(
                    "§4■ Propriétaire"
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
                itemMeta.setLore(List.of(
                        "§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items",
                        "§7■ A gagné §a" + merchantData.getMoneyWon() + "€"
                ));
            }));
        }
        if (companies.size() == 2) return content;
        content.put(28, new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§63. §e"+ companies.get(2).getName());
            itemMeta.setLore(List.of(
                    "§7■ Chiffre d'affaires : §a" + companies.get(2).getTurnover() + "€",
                    "§7■ Marchants : §a" + companies.get(2).getMerchants().size()
            ));
        }));
        UUID ownerUUIDThird;
        if (companies.get(2).getOwner().isTeam()) ownerUUIDThird = companies.get(2).getOwner().getCity().getPlayerWith(CPermission.OWNER);
        else ownerUUIDThird = companies.get(2).getOwner().getPlayer();
        content.put(30, new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUIDThird), itemMeta -> {
            itemMeta.setDisplayName("§6" + (companies.get(2).getOwner().isTeam() ? companies.get(2).getName() : Bukkit.getOfflinePlayer(ownerUUIDThird).getName()));
            itemMeta.setLore(List.of(
                    "§4■ Propriétaire"
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
                itemMeta.setLore(List.of(
                        "§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items",
                        "§7■ A gagné §a" + merchantData.getMoneyWon() + "€"
                ));
            }));
        }
        return content;
    }
}
