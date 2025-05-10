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

public class PersonalBankDepositMenu extends Menu {

    public PersonalBankDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des banques - Banque Personel";
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

        double moneyPlayer = EconomyManager.getInstance().getBalance(player.getUniqueId());
        double halfMoneyPlayer = moneyPlayer/2;

        List<Component> loreBankDepositAll = List.of(
                Component.text("§7Tout votre argent sera placé dans §6Votre Banque"),
                Component.text(""),
                Component.text("§7Montant qui sera deposé : §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
        );

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.HOPPER, 64), itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer tout votre §6Argent"));
            itemMeta.lore(loreBankDepositAll);
        }).setOnClick(inventoryClickEvent -> {
            if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), moneyPlayer) && moneyPlayer!=0) {
                BankManager.getInstance().addBankBalance(player.getUniqueId(), moneyPlayer);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyPlayer) + "§r" + EconomyManager.getEconomyIcon() + " à ta banque"), Prefix.BANK, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.BANK, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));


        List<Component> loreBankDepositHalf = List.of(
                Component.text("§7La moitié de votre Argent sera placé dans §6Votre Banque"),
                Component.text(""),
                Component.text("§7Montant qui sera deposé : §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(halfMoneyPlayer) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
        );

        inventory.put(13, new ItemBuilder(this,new ItemStack(Material.HOPPER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer la moitié de votre §6Argent"));
            itemMeta.lore(loreBankDepositHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), halfMoneyPlayer) && halfMoneyPlayer!=0) {
                BankManager.getInstance().addBankBalance(player.getUniqueId(), halfMoneyPlayer);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(halfMoneyPlayer) + "§r" + EconomyManager.getEconomyIcon() + " à ta banque"), Prefix.BANK, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.BANK, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));


            
        List<Component> loreBankDepositInput = List.of(
            Component.text("§7Votre argent sera placé dans §6Votre Banque"),
            Component.text("§e§lCLIQUEZ ICI POUR INDIQUER LE MONTANT")
        );

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer un §6montant précis"));
            itemMeta.lore(loreBankDepositInput);
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
                            BankManager.getInstance().addBankBalance(player, input);
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
                    Component.text("§7Vous allez retourner au Menu de votre banque"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            PersonalBankMenu menu = new PersonalBankMenu(player);
            menu.open();
        }));

        return inventory;
    }
}
