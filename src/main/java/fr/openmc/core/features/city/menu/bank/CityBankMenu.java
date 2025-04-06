package fr.openmc.core.features.city.menu.bank;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityBankMenu extends Menu {

    public CityBankMenu(Player owner) {
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

        List<Component> loreBankDeposit;

        if (city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE)) {
            loreBankDeposit = List.of(
                    Component.text("§7Votre argent sera placé dans la §6Banque de la Ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankDeposit = List.of(
                    Component.text("§cVous n'avez pas le droit de faire ceci")
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer de l'§6Argent"));
            itemMeta.lore(loreBankDeposit);
        }).setOnClick(inventoryClickEvent -> {
            if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE))) {
                MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityBankDepositMenu menu = new CityBankDepositMenu(player);
            menu.open();
        }));

        if (city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE)) {
            inventory.put(13, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
                itemMeta.itemName(Component.text("§6L'Argent de votre Ville"));
                itemMeta.lore(List.of(
                        Component.text("§7La ville a actuellement §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(city.getBalance()) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false))
                        )
                );
            }));
        }

        List<Component> loreBankTake;

        if (city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE)) {
            loreBankTake = List.of(
                    Component.text("§7L'argent sera pris dans la §6Banque de la Ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR INDIQUER LE MONTANT")
            );
        } else {
            loreBankTake = List.of(
                    Component.text("§cVous n'avez pas le droit de faire ceci")
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.DISPENSER, itemMeta -> {
            itemMeta.itemName(Component.text("§7Retirer de l'§6Argent"));
            itemMeta.lore(loreBankTake);
        }).setOnClick(inventoryClickEvent -> {
            if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE))) {
                MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de prendre de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityBankWithdrawMenu menu = new CityBankWithdrawMenu(player);
            menu.open();
        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au menu des comptes en banque"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            BankMainMenu menu = new BankMainMenu(player);
            menu.open();
        }));

        return inventory;
    }
}