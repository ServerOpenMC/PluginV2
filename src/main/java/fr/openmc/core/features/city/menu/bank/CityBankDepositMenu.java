package fr.openmc.core.features.city.menu.bank;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityBankDepositMenu extends Menu {

    public CityBankDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Banque de Ville";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionMoneyGive = city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE);

        double moneyPlayer = EconomyManager.getInstance().getBalance(player.getUniqueId());
        double halfMoneyPlayer = moneyPlayer/2;

        List<Component> loreBankDepositAll;

        if (hasPermissionMoneyGive) {
            loreBankDepositAll = List.of(
                    Component.text("§7Tout votre argent sera placé dans la §6Banque de la Ville"),
                    Component.text(""),
                    Component.text("§7Montant qui sera deposé : §6" + moneyPlayer + EconomyManager.getEconomyIcon()),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankDepositAll = List.of(
                    Component.text("§cVous n'avez pas le droit de faire ceci")
            );
        }

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.HOPPER, 64), itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer tout votre §6Argent"));
            itemMeta.lore(loreBankDepositAll);
        }).setOnClick(inventoryClickEvent -> {
            if (!hasPermissionMoneyGive) {
                MessagesManager.sendMessageType(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }


            if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), moneyPlayer) && moneyPlayer!=0) {
                city.updateBalance(moneyPlayer);
                MessagesManager.sendMessageType(player, Component.text("Tu as transféré " + moneyPlayer + EconomyManager.getEconomyIcon() + " à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessageType(player, Component.text("Tu n'as pas assez d'argent"), Prefix.CITY, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));


        List<Component> loreBankDepositHalf;

        if (hasPermissionMoneyGive) {
            loreBankDepositHalf = List.of(
                    Component.text("§7La moitié de votre Argent sera placé dans la §6Banque de la Ville"),
                    Component.text(""),
                    Component.text("§7Montant qui sera deposé : §6" + halfMoneyPlayer + EconomyManager.getEconomyIcon()),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankDepositHalf = List.of(
                    Component.text("§cVous n'avez pas le droit de faire ceci")
            );
        }

        inventory.put(13, new ItemBuilder(this,new ItemStack(Material.HOPPER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer la moitié de votre §6Argent"));
            itemMeta.lore(loreBankDepositHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (!hasPermissionMoneyGive) {
                MessagesManager.sendMessageType(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), halfMoneyPlayer) && halfMoneyPlayer!=0) {
                city.updateBalance(halfMoneyPlayer);
                MessagesManager.sendMessageType(player, Component.text("Tu as transféré " + halfMoneyPlayer + EconomyManager.getEconomyIcon() + " à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessageType(player, Component.text("Tu n'as pas assez d'argent"), Prefix.CITY, MessageType.ERROR, false);
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
                    Component.text("§cVous n'avez pas le droit de faire ceci")
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer un §6montant précis"));
            itemMeta.lore(loreBankDepositInput);
        }).setOnClick(inventoryClickEvent -> {
            if (!hasPermissionMoneyGive) {
                MessagesManager.sendMessageType(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

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

                            if (InputUtils.isInputMoney(input)) {
                                double moneyDeposit = InputUtils.convertToMoneyValue(input);

                                if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), moneyDeposit)) {
                                    city.updateBalance(moneyDeposit);
                                    MessagesManager.sendMessageType(player, Component.text("Tu as transféré "+moneyDeposit+EconomyManager.getEconomyIcon()+" à ta ville"), Prefix.CITY, MessageType.ERROR, false);
                                } else {
                                    MessagesManager.sendMessageType(player, Component.text("Tu n'as pas assez d'argent"), Prefix.CITY, MessageType.ERROR, false);
                                }
                            } else {
                                MessagesManager.sendMessageType(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
                            }

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
                    Component.text("§7Vous allez retourner au Menu de la Banque de votre ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityBankMenu menu = new CityBankMenu(player);
            menu.open();
        }));

        return inventory;
    }
}