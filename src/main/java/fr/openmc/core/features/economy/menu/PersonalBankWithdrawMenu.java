package fr.openmc.core.features.economy.menu;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

public class PersonalBankWithdrawMenu extends Menu {

    public PersonalBankWithdrawMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.economy.bank.withdraw.menu.title");
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

        double moneyBankPlayer = BankManager.getBankBalance(player.getUniqueId());
        double halfMoneyBankPlayer = moneyBankPlayer/2;

        List<Component> loreBankWithdrawAll = TranslationManager.translationLore(
                "feature.economy.bank.withdraw.all.lore",
                Component.text(EconomyManager.getFormattedSimplifiedNumber(moneyBankPlayer)).color(NamedTextColor.LIGHT_PURPLE),
                Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)
        );

        inventory.put(11, new ItemMenuBuilder(this, new ItemStack(Material.DISPENSER, 64), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.withdraw.all.name"));
            itemMeta.lore(loreBankWithdrawAll);
        }).setOnClick(inventoryClickEvent -> {
            player.closeInventory();
            BankManager.withdraw(player.getUniqueId(), String.valueOf(moneyBankPlayer));
        }));

        List<Component> loreBankWithdrawHalf = TranslationManager.translationLore(
                "feature.economy.bank.withdraw.half.lore",
                Component.text(EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankPlayer)).color(NamedTextColor.LIGHT_PURPLE),
                Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)
        );

        inventory.put(13, new ItemMenuBuilder(this,new ItemStack(Material.DISPENSER, 32), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.withdraw.half.name"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            BankManager.withdraw(player.getUniqueId(), String.valueOf(halfMoneyBankPlayer));
            player.closeInventory();
        }));


        List<Component> loreBankWithdrawInput = TranslationManager.translationLore(
                "feature.economy.bank.withdraw.input.lore"
        );

        inventory.put(15, new ItemMenuBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.withdraw.input.name"));
            itemMeta.lore(loreBankWithdrawInput);
        }).setOnClick(inventoryClickEvent -> {
            DialogInput.send(player, TranslationManager.translation("feature.economy.bank.withdraw.input.prompt"), MAX_LENGTH, input -> {
                if (input == null) return;

                BankManager.withdraw(player.getUniqueId(), input);
            });
        }));

        inventory.put(18, new ItemMenuBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(TranslationManager.translationLore("feature.economy.bank.back.lore"));
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
