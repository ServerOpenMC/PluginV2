package fr.openmc.core.features.city.sub.bank.menu;

import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH;

public class CityBankDepositMenu extends Menu {

    public CityBankDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Banque de Ville - Remplir";
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
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionMoneyGive = city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE);

        double moneyPlayer = EconomyManager.getBalance(player.getUniqueId());
        double halfMoneyPlayer = moneyPlayer / 2;

        List<Component> loreBankDepositAll;

        if (hasPermissionMoneyGive) {
            loreBankDepositAll = List.of(
                    Component.text("§7Tout votre argent sera placé dans la §6Banque de la Ville"),
                    Component.empty(),
                    Component.text("§7Montant qui sera deposé : §d" + EconomyManager.getFormattedSimplifiedNumber(moneyPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankDepositAll = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.HOPPER, 64), itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer tout votre §6Argent"));
            itemMeta.lore(loreBankDepositAll);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityDeposit(city, player)) return;

            if (EconomyManager.withdrawBalance(player.getUniqueId(), moneyPlayer) && moneyPlayer != 0) {
                city.updateBalance(moneyPlayer);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getFormattedSimplifiedNumber(moneyPlayer) + "§r" + EconomyManager.getEconomyIcon() + " à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));


        List<Component> loreBankDepositHalf;

        if (hasPermissionMoneyGive) {
            loreBankDepositHalf = List.of(
                    Component.text("§7La moitié de votre Argent sera placé dans la §6Banque de la Ville"),
                    Component.empty(),
                    Component.text("§7Montant qui sera deposé : §d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankDepositHalf = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(13, new ItemBuilder(this, new ItemStack(Material.HOPPER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer la moitié de votre §6Argent"));
            itemMeta.lore(loreBankDepositHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityDeposit(city, player)) return;

            if (EconomyManager.withdrawBalance(player.getUniqueId(), halfMoneyPlayer) && halfMoneyPlayer != 0) {
                city.updateBalance(halfMoneyPlayer);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyPlayer) + "§r" + EconomyManager.getEconomyIcon() + " à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));


        List<Component> loreBankDepositInput;

        if (hasPermissionMoneyGive) {
            loreBankDepositInput = List.of(
                    Component.text("§7Votre argent sera placé dans la §6Banque de la Ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR INDIQUER LE MONTANT")
            );
        } else {
            loreBankDepositInput = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer un §6montant précis"));
            itemMeta.lore(loreBankDepositInput);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityDeposit(city, player)) return;

            DialogInput.send(player, Component.text("Entrez le montant que vous voulez déposer"), MAX_LENGTH, input ->
                    city.depositCityBank(player, input)
            );

        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de la Banque de votre ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityBankMenu menu = new CityBankMenu(player);
            menu.open();
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