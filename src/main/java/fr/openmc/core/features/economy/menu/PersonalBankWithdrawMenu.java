package fr.openmc.core.features.economy.menu;

import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.economy.BankManager;
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

public class PersonalBankWithdrawMenu extends Menu {

    public PersonalBankWithdrawMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Banques - Banque Personel";
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

        double moneyBankPlayer = BankManager.getBankBalance(player.getUniqueId());
        double halfMoneyBankPlayer = moneyBankPlayer/2;

        List<Component> loreBankWithdrawAll = List.of(
                Component.text("§7Tout l'argent placé dans §6Votre Banque §7vous sera donné"),
                Component.empty(),
                Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getFormattedSimplifiedNumber(moneyBankPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                Component.empty(),
                Component.text("§e§lCLIQUEZ ICI POUR PRENDRE")
        );

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 64), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre l'§6Argent de votre banque"));
            itemMeta.lore(loreBankWithdrawAll);
        }).setOnClick(inventoryClickEvent -> {
            if (halfMoneyBankPlayer != 0) {
                BankManager.withdrawBankBalance(player.getUniqueId(), moneyBankPlayer);
                EconomyManager.addBalance(player.getUniqueId(), moneyBankPlayer);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(moneyBankPlayer)
                            + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.BANK, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Impossible de vous transféré l'argent, votre banque est vide"), Prefix.BANK, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));

        List<Component> loreBankWithdrawHalf = List.of(
            Component.text("§7La Moitié de l'Argent sera pris de §6Votre Banque §7pour vous le donner"),
                Component.empty(),
            Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                Component.empty(),
            Component.text("§e§lCLIQUEZ ICI POUR PRENDRE")
        );

        inventory.put(13, new ItemBuilder(this,new ItemStack(Material.DISPENSER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre la moitié de l'§6Argent de votre banque"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (halfMoneyBankPlayer != 0) {
                BankManager.withdrawBankBalance(player.getUniqueId(), halfMoneyBankPlayer);
                EconomyManager.addBalance(player.getUniqueId(), halfMoneyBankPlayer);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankPlayer) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.BANK, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Impossible de vous transféré l'argent, votre banque est vide"), Prefix.BANK, MessageType.ERROR, false);
            }

            player.closeInventory();
        }));


        List<Component> loreBankWithdrawInput = List.of(
            Component.text("§7L'argent demandé sera pris dans §6Votre Banque §7pour vous le donner"),
            Component.text("§e§lCLIQUEZ ICI POUR INDIQUER LE MONTANT")
        );

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre un §6montant précis"));
            itemMeta.lore(loreBankWithdrawInput);
        }).setOnClick(inventoryClickEvent -> {
            DialogInput.send(player, Component.text("Entrez le montant que vous voulez retirer"), MAX_LENGTH, input ->
                    BankManager.withdrawBankBalance(player, input)
            );
        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de votre Banque"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            new PersonalBankMenu(player).open();
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
