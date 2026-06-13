package fr.openmc.core.features.economy.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.rewards.PlayerBankLimitRewards;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PersonalBankMenu extends Menu {

    public PersonalBankMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.economy.bank.menu.title");
    }

    @Override
    public String getTexture() {
        return null;
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
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        List<Component> loreBankDeposit = TranslationManager.translationLore("feature.economy.bank.menu.deposit.lore");

        inventory.put(11, new ItemMenuBuilder(this, Material.HOPPER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.menu.deposit.name"));
            itemMeta.lore(loreBankDeposit);
        }).setOnClick(inventoryClickEvent -> {
            new PersonalBankDepositMenu(player).open();
        }));

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.economy.bank.menu.need_city"),
                    Prefix.BANK, MessageType.ERROR, false);
            return Map.of();
        }

        Supplier<ItemMenuBuilder> interestItemSupplier = () -> {
            return new ItemMenuBuilder(this, Material.DIAMOND_BLOCK, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.menu.balance.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.economy.bank.menu.balance.lore",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(BankManager.getBankBalance(player.getUniqueId()))).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false),
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(PlayerBankLimitRewards.getBankBalanceLimit(playerCity.getLevel()))).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false),
                    Component.text(BankManager.calculatePlayerInterest(player.getUniqueId()) * 100 + "%").color(NamedTextColor.AQUA),
                    Component.text(DateUtils.convertSecondToTime(BankManager.getSecondsUntilInterest())).color(NamedTextColor.AQUA)
            ));
            });
        };

        MenuUtils.runDynamicItem(player, this, 13, interestItemSupplier)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

        List<Component> loreBankTake = TranslationManager.translationLore("feature.economy.bank.menu.withdraw.lore");

        inventory.put(15, new ItemMenuBuilder(this, Material.DISPENSER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.menu.withdraw.name"));
            itemMeta.lore(loreBankTake);
        }).setOnClick(inventoryClickEvent -> {
            new PersonalBankWithdrawMenu(player).open();
        }));

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
