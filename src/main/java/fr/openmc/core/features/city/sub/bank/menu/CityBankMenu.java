package fr.openmc.core.features.city.sub.bank.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CityBankMenu extends Menu {

    public CityBankMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.bank.menu.name");
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

        List<Component> loreBankDeposit;

        if (city.hasPermission(player.getUniqueId(), CityPermission.MONEY_DEPOSIT)) {
            loreBankDeposit = TranslationManager.translationLore("feature.city.bank.menu.deposit.lore");
        } else {
            loreBankDeposit = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(11, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.deposit.title"));
            itemMeta.lore(loreBankDeposit);
        }).setOnClick(inventoryClickEvent -> {
            if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_DEPOSIT))) {
                MessagesManager.sendMessage(player,
                        TranslationManager.translation("feature.city.bank.errors.no_permission_deposit"),
                        Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityBankDepositMenu menu = new CityBankDepositMenu(player);
            menu.open();
        }));

        if (city.hasPermission(player.getUniqueId(), CityPermission.MONEY_BALANCE)) {

            Supplier<ItemBuilder> interestItemSupplier = () -> new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
                itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.balance.title"));
                itemMeta.lore(TranslationManager.translationLore(
                        "feature.city.bank.menu.balance.lore",
                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance())).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(city.calculateCityInterest() * 100).color(NamedTextColor.AQUA),
                        Component.text(DateUtils.convertSecondToTime(BankManager.getSecondsUntilInterest())).color(NamedTextColor.AQUA)
                ));
            });

            MenuUtils.runDynamicItem(player, this, 13, interestItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        }

        List<Component> loreBankTake;

        if (city.hasPermission(player.getUniqueId(), CityPermission.MONEY_WITHDRAW)) {
            loreBankTake = TranslationManager.translationLore("feature.city.bank.menu.withdraw.lore");
        } else {
            loreBankTake = TranslationManager.translationLore("messages.global.cannot_do_this");
        }

        inventory.put(15, new ItemBuilder(this, Material.DISPENSER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.bank.menu.withdraw.title"));
            itemMeta.lore(loreBankTake);
        }).setOnClick(inventoryClickEvent -> {
            if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_WITHDRAW))) {
                MessagesManager.sendMessage(player,
                        TranslationManager.translation("feature.city.bank.errors.no_permission_withdraw"),
                        Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityBankWithdrawMenu menu = new CityBankWithdrawMenu(player);
            menu.open();
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