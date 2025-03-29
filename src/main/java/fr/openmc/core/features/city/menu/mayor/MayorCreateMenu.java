package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorCreateMenu extends Menu {
    private final Perks perk2;
    private final Perks perk3;
    public MayorCreateMenu(Player owner, Perks perk2, Perks perk3) {
        super(owner);
        this.perk2 = perk2;
        this.perk3 = perk3;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        boolean canConfirmPerk = perk2 != null && perk3 != null;

        Material matPerk2 = (perk2 != null) ? perk2.getMaterial() : Material.DEAD_BRAIN_CORAL_BLOCK;
        String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme Vide";
        List<Component> lorePerk2;
        if (perk2 == null) {
            lorePerk2 = List.of(
                    Component.text("§7Choissiez §3votre Réforme §7que vous voulez voir !"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
            );
        } else {
            lorePerk2 = new ArrayList<>(perk2.getLore());
            lorePerk2.add(Component.text(""));
            lorePerk2.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
        }
        inventory.put(11, new ItemBuilder(this, matPerk2, itemMeta -> {
            itemMeta.itemName(Component.text(namePerk2));
            itemMeta.lore(lorePerk2);
        }).setOnClick(inventoryClickEvent -> {
            new PerkChoiceMenu(player,"perk2", perk2, perk3).open();
        }));

        Material matPerk3 = (perk3 != null) ? perk3.getMaterial() : Material.DEAD_BRAIN_CORAL_BLOCK;
        String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme Vide";
        List<Component> lorePerk3;
        if (perk3 == null) {
            lorePerk3 = List.of(
                    Component.text("§7Choissiez §3votre Réforme §7que vous voulez voir !"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
            );
        } else {
            lorePerk3 = new ArrayList<>(perk3.getLore());
            lorePerk3.add(Component.text(""));
            lorePerk3.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
        }
        inventory.put(15, new ItemBuilder(this, matPerk3,itemMeta -> {
            itemMeta.itemName(Component.text(namePerk3));
            itemMeta.lore(lorePerk3);
        }).setOnClick(inventoryClickEvent -> {
            new PerkChoiceMenu(player, "perk3", perk2, perk3).open();
        }));

        Material matConfirm;
        String nameConfirm;
        List<Component> loreConfirm;
        if (canConfirmPerk) {
            matConfirm = CustomItemRegistry.getByName("omc_menus:accept_btn").getBest().getType();
            nameConfirm = "§aConfirmer";
            loreConfirm = List.of(
                    Component.text("§7Vous avez choisis toutes §ales Réformes §7nécessaires"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CONTINUER LA CANDIDATURE")
            );
        } else {
            matConfirm = CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest().getType();
            nameConfirm = "§cConfirmer";
            loreConfirm = List.of(
                    Component.text("§7Vous n'avez pas choisis toutes §cles Réformes §7nécessaires!")
            );
        }

        inventory.put(26, new ItemBuilder(this, matConfirm, itemMeta -> {
            itemMeta.itemName(Component.text(nameConfirm));
            itemMeta.lore(loreConfirm);
        }).setOnClick(inventoryClickEvent -> {
            if (canConfirmPerk) {
                new MayorColorMenu(player, perk2, perk3).open();
            }
        }));


        return inventory;
    }
}
