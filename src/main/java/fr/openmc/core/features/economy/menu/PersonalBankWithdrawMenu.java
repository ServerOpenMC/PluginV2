package fr.openmc.core.features.economy.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.signgui.SignGUI;
import fr.openmc.api.signgui.exception.SignGUIVersionException;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        double moneyBankPlayer = BankManager.getInstance().getBankBalance(player.getUniqueId());
        double halfMoneyBankPlayer = moneyBankPlayer/2;

        List<Component> loreBankWithdrawAll = List.of(
                Component.text("§7Tout l'argent placé dans §6Votre Banque §7vous sera donné"),
                Component.text(""),
                Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyBankPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR PRENDRE")
        );

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 64), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre l'§6Argent de votre banque"));
            itemMeta.lore(loreBankWithdrawAll);
        }).setOnClick(inventoryClickEvent -> {
            if (halfMoneyBankPlayer != 0) {
                BankManager.getInstance().withdrawBankBalance(player.getUniqueId(), moneyBankPlayer);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), moneyBankPlayer);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyBankPlayer)
                            + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.BANK, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Impossible de vous transféré l'argent, votre banque est vide"), Prefix.BANK, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));

        List<Component> loreBankWithdrawHalf = List.of(
            Component.text("§7La Moitié de l'Argent sera pris de §6Votre Banque §7pour vous le donner"),
            Component.text(""),
            Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(halfMoneyBankPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
            Component.text(""),
            Component.text("§e§lCLIQUEZ ICI POUR PRENDRE")
        );

        inventory.put(13, new ItemBuilder(this,new ItemStack(Material.DISPENSER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre la moitié de l'§6Argent de votre banque"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (halfMoneyBankPlayer != 0) {
                BankManager.getInstance().withdrawBankBalance(player.getUniqueId(), halfMoneyBankPlayer);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), halfMoneyBankPlayer);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(halfMoneyBankPlayer) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.BANK, MessageType.SUCCESS, false);
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

            String[] lines = new String[4];
            lines[0] = "";
            lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
            lines[2] = "Entrez votre";
            lines[3] = "montant ci dessus";

            SignGUI gui = null;
            try {
                gui = SignGUI.builder()
                        .setLines(null, lines[1] , lines[2], lines[3])
                        .setType(ItemUtils.getSignType(player))
                        .setHandler((p, result) -> {
                            String input = result.getLine(0);
                            BankManager.getInstance().withdrawBankBalance(player, input);
                            return Collections.emptyList();
                        })
                        .build();
            } catch (SignGUIVersionException e) {
                throw new RuntimeException(e);
            }

            gui.open(player);

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
}
