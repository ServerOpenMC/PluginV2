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

public class CityBankWithdrawMenu extends Menu {

    public CityBankWithdrawMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Banque de Ville - Retirer";
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

        boolean hasPermissionMoneyTake = city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE);

        double moneyBankCity = city.getBalance();
        double halfMoneyBankCity = moneyBankCity / 2;

        List<Component> loreBankWithdrawAll;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawAll = List.of(
                    Component.text("§7Tout l'argent placé dans la §6Banque de la Ville §7vous sera donné"),
                    Component.empty(),
                    Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getFormattedSimplifiedNumber(moneyBankCity) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR PRENDRE")
            );
        } else {
            loreBankWithdrawAll = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 64), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre l'§6Argent de votre Ville"));
            itemMeta.lore(loreBankWithdrawAll);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityWithdraw(city, player)) return;

            if (halfMoneyBankCity != 0) {
                city.updateBalance(moneyBankCity * -1);
                EconomyManager.addBalance(player.getUniqueId(), moneyBankCity);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(moneyBankCity) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Impossible de vous transféré l'argent, le solde de la ville est vide"), Prefix.CITY, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));

        List<Component> loreBankWithdrawHalf;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawHalf = List.of(
                    Component.text("§7La Moitié de l'Argent sera pris de la §6Banque de votre Ville §7pour vous le donner"),
                    Component.empty(),
                    Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankCity) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankWithdrawHalf = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(13, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre la moitié de l'§6Argent de la Ville"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityWithdraw(city, player)) return;

            if (halfMoneyBankCity != 0) {
                city.updateBalance(halfMoneyBankCity * -1);
                EconomyManager.addBalance(player.getUniqueId(), halfMoneyBankCity);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankCity) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Impossible de vous transféré l'argent, le solde de la ville est vide"), Prefix.CITY, MessageType.ERROR, false);
            }

            player.closeInventory();
        }));


        List<Component> loreBankWithdrawInput;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawInput = List.of(
                    Component.text("§7L'argent demandé sera pris dans la §6Banque de la Ville §7pour vous le donner"),
                    Component.text("§e§lCLIQUEZ ICI POUR INDIQUER LE MONTANT")
            );
        } else {
            loreBankWithdrawInput = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre un §6montant précis"));
            itemMeta.lore(loreBankWithdrawInput);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityWithdraw(city, player)) return;

            DialogInput.send(player, Component.text("Entrez le montant que vous voulez retirer"), MAX_LENGTH, input ->
                    city.withdrawCityBank(player, input)
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