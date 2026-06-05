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

public class PersonalBankDepositMenu extends Menu {

    public PersonalBankDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.economy.bank.deposit.menu.title");
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

        double moneyPlayer = EconomyManager.getBalance(player.getUniqueId());
        double halfMoneyPlayer = moneyPlayer/2;

        List<Component> loreBankDepositAll = TranslationManager.translationLore(
                "feature.economy.bank.deposit.all.lore",
                Component.text(EconomyManager.getFormattedSimplifiedNumber(moneyPlayer)).color(NamedTextColor.LIGHT_PURPLE),
                Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)
        );

        inventory.put(11, new ItemMenuBuilder(this, new ItemStack(Material.HOPPER, 64), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.deposit.all.name"));
            itemMeta.lore(loreBankDepositAll);
        }).setOnClick(inventoryClickEvent -> {
            BankManager.deposit(player.getUniqueId(), String.valueOf(moneyPlayer));
            player.closeInventory();
        }));


        List<Component> loreBankDepositHalf = TranslationManager.translationLore(
                "feature.economy.bank.deposit.half.lore",
                Component.text(EconomyManager.getFormattedSimplifiedNumber(halfMoneyPlayer)).color(NamedTextColor.LIGHT_PURPLE),
                Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)
        );

        inventory.put(13, new ItemMenuBuilder(this,new ItemStack(Material.HOPPER, 32), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.deposit.half.name"));
            itemMeta.lore(loreBankDepositHalf);
        }).setOnClick(inventoryClickEvent -> {
            BankManager.deposit(player.getUniqueId(), String.valueOf(halfMoneyPlayer));
            player.closeInventory();
        }));
            
        List<Component> loreBankDepositInput = TranslationManager.translationLore("feature.economy.bank.deposit.input.lore");

        inventory.put(15, new ItemMenuBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.economy.bank.deposit.input.name"));
            itemMeta.lore(loreBankDepositInput);
        }).setOnClick(inventoryClickEvent -> {
            DialogInput.send(player, TranslationManager.translation("feature.economy.bank.deposit.input.prompt"), MAX_LENGTH, input -> {
                        if (input == null) return;

                        BankManager.deposit(player.getUniqueId(), input);
                    }
            );
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
