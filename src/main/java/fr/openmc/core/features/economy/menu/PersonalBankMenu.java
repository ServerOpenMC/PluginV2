package fr.openmc.core.features.economy.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
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

public class PersonalBankMenu extends Menu {

    public PersonalBankMenu(Player owner) {
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

        List<Component> loreBankDeposit = List.of(
                Component.text("§7Votre argent sera placé dans votre banque"),
                Component.text("§e§lCLIQUEZ ICI POUR DEPOSER")
        );

        inventory.put(11, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
            itemMeta.itemName(Component.text("§7Déposer de l'§6Argent"));
            itemMeta.lore(loreBankDeposit);
        }).setOnClick(inventoryClickEvent -> {
            new PersonalBankDepositMenu(player).open();
        }));

        inventory.put(13, new ItemBuilder(this, Material.DIAMOND_BLOCK, itemMeta -> {
            itemMeta.itemName(Component.text("§6Votre argent"));
            itemMeta.lore(List.of(
                Component.text("§7Vous avez actuellement §d" +
                        EconomyManager.getInstance().getFormattedSimplifiedNumber(BankManager.getInstance().getBankBalance(player.getUniqueId())) + " ")
                    .append(Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false)),
                Component.text("§7Votre prochain intéret est de §b" + BankManager.getInstance().calculatePlayerInterest(player.getUniqueId())*100 + "% §7dans §b" + DateUtils.convertSecondToTime(BankManager.getInstance().getSecondsUntilInterest()))
                )
            );
        }));

        List<Component> loreBankTake = List.of(
                Component.text("§7L'argent sera pris dans votre banque"),
                Component.text("§e§lCLIQUEZ ICI POUR RETIRER")
        );

        inventory.put(15, new ItemBuilder(this, Material.DISPENSER, itemMeta -> {
            itemMeta.itemName(Component.text("§7Retirer de l'§6Argent"));
            itemMeta.lore(loreBankTake);
        }).setOnClick(inventoryClickEvent -> {
            new PersonalBankWithdrawMenu(player).open();
        }));

        return inventory;
    }
}
