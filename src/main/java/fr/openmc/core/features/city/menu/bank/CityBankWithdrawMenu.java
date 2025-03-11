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

public class CityBankWithdrawMenu extends Menu {

    public CityBankWithdrawMenu(Player owner) {
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
        double halfMoneyBankCity =  moneyBankCity/2;

        List<Component> loreBankWithdrawAll;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawAll = List.of(
                    Component.text("§7Tout l'argent placé dans la §6Banque de la Ville §7vous sera donné"),
                    Component.text(""),
                    Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyBankCity) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.text(""),
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
            if (!hasPermissionMoneyTake) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOMONEYTAKE.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            if (halfMoneyBankCity != 0) {
                city.updateBalance(moneyBankCity*-1);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), moneyBankCity);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyBankCity) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Impossible de vous transféré l'argent, le solde de la ville est vide"), Prefix.CITY, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));


        List<Component> loreBankWithdrawHalf;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawHalf = List.of(
                    Component.text("§7La Moitié de l'Argent sera pris de la §6Banque de votre Ville §7pour vous le donner"),
                    Component.text(""),
                    Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(halfMoneyBankCity) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankWithdrawHalf = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(13, new ItemBuilder(this,new ItemStack(Material.DISPENSER, 32), itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre la moitié de l'§6Argent de la Ville"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            if (!hasPermissionMoneyTake ) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOMONEYTAKE.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (halfMoneyBankCity != 0) {
                city.updateBalance(halfMoneyBankCity * -1);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), halfMoneyBankCity);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(halfMoneyBankCity) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
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
            if (!hasPermissionMoneyTake) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOMONEYTAKE.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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

                                if (city.getBalance() < moneyDeposit) {
                                    MessagesManager.sendMessage(player, Component.text("Ta ville n'a pas assez d'argent en banque"), Prefix.CITY, MessageType.ERROR, false);
                                } else {
                                    city.updateBalance(moneyDeposit * -1);
                                    EconomyManager.getInstance().addBalance(player.getUniqueId(), moneyDeposit);
                                    MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
                                }
                            } else {
                                MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
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