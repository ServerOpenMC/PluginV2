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

public class CityBankDepositMenu extends Menu {

    public CityBankDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.bank.menu.deposit.name");
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

        boolean hasPermissionMoneyGive = city.hasPermission(player.getUniqueId(), CityPermission.MONEY_DEPOSIT);

        double moneyPlayer = EconomyManager.getBalance(player.getUniqueId());
        double halfMoneyPlayer = moneyPlayer / 2;

        List<Component> loreBankDepositAll;

        if (hasPermissionMoneyGive) {
            loreBankDepositAll = TranslationManager.translationLore(
                    "feature.city.bank.menu.deposit.all.lore",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(moneyPlayer)).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            loreBankDepositAll = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.HOPPER, 64), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.deposit.all.title"));
            itemMeta.lore(loreBankDepositAll);
        }).setOnClick(inventoryClickEvent -> {
            city.depositCityBank(player, String.valueOf(moneyPlayer));
            player.closeInventory();
        }));


        List<Component> loreBankDepositHalf;

        if (hasPermissionMoneyGive) {
            loreBankDepositHalf = TranslationManager.translationLore(
                    "feature.city.bank.menu.deposit.half.lore",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(halfMoneyPlayer)).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            loreBankDepositHalf = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(13, new ItemBuilder(this, new ItemStack(Material.HOPPER, 32), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.deposit.half.title"));
            itemMeta.lore(loreBankDepositHalf);
        }).setOnClick(inventoryClickEvent -> {
            city.depositCityBank(player, String.valueOf(halfMoneyPlayer));
            player.closeInventory();
        }));


        List<Component> loreBankDepositInput;

        if (hasPermissionMoneyGive) {
            loreBankDepositInput = TranslationManager.translationLore("feature.city.bank.menu.deposit.input.lore");
        } else {
            loreBankDepositInput = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.deposit.input.title"));
            itemMeta.lore(loreBankDepositInput);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityDeposit(city, player)) return;

            DialogInput.send(player, TranslationManager.translation("feature.city.bank.menu.deposit.input.prompt"), MAX_LENGTH, input -> {
                        if (input == null) return;
                city.depositCityBank(player, input);
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