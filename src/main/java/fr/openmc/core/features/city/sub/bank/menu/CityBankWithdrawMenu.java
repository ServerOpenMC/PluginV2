package fr.openmc.core.features.city.sub.bank.menu;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.utils.text.InputUtils.MAX_LENGTH;

public class CityBankWithdrawMenu extends Menu {

    public CityBankWithdrawMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.bank.menu.withdraw.name");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionMoneyTake = city.hasPermission(player.getUniqueId(), CityPermission.MONEY_WITHDRAW);

        double moneyBankCity = city.getBalance();
        double halfMoneyBankCity = moneyBankCity / 2;

        List<Component> loreBankWithdrawAll;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawAll = TranslationManager.translationLore(
                    "feature.city.bank.menu.withdraw.all.lore",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(moneyBankCity)).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            loreBankWithdrawAll = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 64), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.withdraw.all.title"));
            itemMeta.lore(loreBankWithdrawAll);
        }).setOnClick(inventoryClickEvent -> {
            city.withdrawCityBank(player, String.valueOf(moneyBankCity));
            player.closeInventory();
        }));

        List<Component> loreBankWithdrawHalf;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawHalf = TranslationManager.translationLore(
                    "feature.city.bank.menu.withdraw.half.lore",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankCity)).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            loreBankWithdrawHalf = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(13, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 32), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.withdraw.half.title"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            city.withdrawCityBank(player, String.valueOf(halfMoneyBankCity));
            player.closeInventory();
        }));


        List<Component> loreBankWithdrawInput;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawInput = TranslationManager.translationLore("feature.city.bank.menu.withdraw.input.lore");
        } else {
            loreBankWithdrawInput = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.withdraw.input.title"));
            itemMeta.lore(loreBankWithdrawInput);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityWithdraw(city, player)) return;

            DialogInput.send(player, TranslationManager.translation("feature.city.bank.menu.withdraw.input.prompt"), MAX_LENGTH, input -> {
                        if (input == null) return;
                        city.withdrawCityBank(player, input);
                    }
            );

        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(TranslationManager.translationLore("feature.city.bank.menu.back_lore"));
        }, true));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}