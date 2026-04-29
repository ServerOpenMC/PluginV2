package fr.openmc.core.features.city.sub.bank.menu;

import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessagesManager;
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
	    return "Menu de la banque de ville - Retirer";
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionMoneyTake = city.hasPermission(player.getUniqueId(), CityPermission.MONEY_TAKE);

        double moneyBankCity = city.getBalance();
        double halfMoneyBankCity = moneyBankCity / 2;

        List<Component> loreBankWithdrawAll;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawAll = List.of(
		            Component.text("§7Tout l'argent placé dans la §6banque de la ville §7vous sera donné"),
                    Component.empty(),
                    Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getFormattedSimplifiedNumber(moneyBankCity) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR PRENDRE")
            );
        } else {
            loreBankWithdrawAll = List.of(
                    MessagesManager.Message.NO_PERMISSION_2.getMessage()
            );
        }

        inventory.put(11, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 64), itemMeta -> {
	        itemMeta.itemName(Component.text("§7Prendre l'§6argent de votre ville"));
            itemMeta.lore(loreBankWithdrawAll);
        }).setOnClick(inventoryClickEvent -> {
            city.withdrawCityBank(player, String.valueOf(moneyBankCity));
            player.closeInventory();
        }));

        List<Component> loreBankWithdrawHalf;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawHalf = List.of(
		            Component.text("§7La moitié de l'argent sera pris de la §6banque de votre ville §7pour vous le donner"),
                    Component.empty(),
                    Component.text("§7Montant qui vous sera donné : §d" + EconomyManager.getFormattedSimplifiedNumber(halfMoneyBankCity) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
            );
        } else {
            loreBankWithdrawHalf = List.of(
                    MessagesManager.Message.NO_PERMISSION_2.getMessage()
            );
        }

        inventory.put(13, new ItemBuilder(this, new ItemStack(Material.DISPENSER, 32), itemMeta -> {
	        itemMeta.itemName(Component.text("§7Prendre la moitié de l'§6argent de la ville"));
            itemMeta.lore(loreBankWithdrawHalf);
        }).setOnClick(inventoryClickEvent -> {
            city.withdrawCityBank(player, String.valueOf(halfMoneyBankCity));
            player.closeInventory();
        }));


        List<Component> loreBankWithdrawInput;

        if (hasPermissionMoneyTake) {
            loreBankWithdrawInput = List.of(
		            Component.text("§7L'argent demandé sera pris dans la §6banque de la ville §7pour vous le donner"),
                    Component.text("§e§lCLIQUEZ ICI POUR INDIQUER LE MONTANT")
            );
        } else {
            loreBankWithdrawInput = List.of(
                    MessagesManager.Message.NO_PERMISSION_2.getMessage()
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Prendre un §6montant précis"));
            itemMeta.lore(loreBankWithdrawInput);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityBankConditions.canCityWithdraw(city, player)) return;

            DialogInput.send(player, Component.text("Entrez le montant que vous voulez retirer"), MAX_LENGTH, input -> {
                        if (input == null) return;
                        city.withdrawCityBank(player, input);
                    }
            );

        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
		            Component.text("§7Vous allez retourner au menu précédent"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
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