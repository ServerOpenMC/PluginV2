package fr.openmc.core.features.city.menu.bank;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.economy.EconomyManager;
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

public class BankMainMenu extends Menu {

    public BankMainMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Banque";
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
        boolean hasPermissionMoneyTake = city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE);
        boolean hasPermissionMoneyBalance = city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE);

        List<Component> loreBankCity;

        if (hasPermissionMoneyBalance) {
            if (hasPermissionMoneyTake || hasPermissionMoneyGive) {
                loreBankCity = List.of(
                        Component.text("§7Votre ville a en stock §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(city.getBalance()) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                        Component.text("§7Prochain intéret dans ..."), //todo: faire un intéret de 2% ou 3% tout les 3j?
                        Component.text("§e§lCLIQUEZ ICI POUR GERER L'ARGENT")
                );
            } else {
                loreBankCity = List.of(
                        Component.text("§7Votre ville a en stock §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(city.getBalance()) + " ").append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                        Component.text("§7Prochain intéret dans ...") //todo: faire un intéret de 2% ou 3% tout les 3j?
                );
            }
        } else {
            if (hasPermissionMoneyTake || hasPermissionMoneyGive) {
                loreBankCity = List.of(
                        Component.text("§7Vous n'avez pas le §cdroit de visionner la banque!"),
                        Component.text("§e§lCLIQUEZ ICI POUR GERER L'ARGENT")
                );
            } else {
                loreBankCity = List.of(
                        Component.text("§7Vous n'avez pas le §cdroit de visionner la banque!")
                );
            }
        }

        inventory.put(11, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.itemName(Component.text("§6La Banque de votre Ville"));
            itemMeta.lore(loreBankCity);
        }).setOnClick(inventoryClickEvent -> {
            if (hasPermissionMoneyTake || hasPermissionMoneyGive) {
                CityBankMenu menu = new CityBankMenu(player);
                menu.open();
            }
        }));

        inventory.put(15, new ItemBuilder(this, Material.DIAMOND_BLOCK, itemMeta -> {
            itemMeta.itemName(Component.text("§bVotre compte personnel"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous avez actuellement §b..."),
                    Component.text("§7Votre prochain intéret est de §b...")
            ));
        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au menu des villes"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
        }));

        return inventory;
    }

}