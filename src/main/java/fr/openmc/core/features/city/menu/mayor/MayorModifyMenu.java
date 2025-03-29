package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mayor.MayorElector;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorModifyMenu extends Menu {
    public MayorModifyMenu(Player owner) {
        super(owner);
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

        MayorElector mayorElector = MayorManager.getInstance().getElector(player);
        Perks perk2 = PerkManager.getPerkById(mayorElector.getIdChoicePerk2());
        Perks perk3 = PerkManager.getPerkById(mayorElector.getIdChoicePerk3());

        inventory.put(11, new ItemBuilder(this, perk2.getMaterial(), itemMeta -> {
            itemMeta.itemName(Component.text(perk2.getName()));
            itemMeta.lore(perk2.getLore());
        }));

        inventory.put(13, new ItemBuilder(this, perk3.getMaterial(),itemMeta -> {
            itemMeta.itemName(Component.text(perk3.getName()));
            itemMeta.lore(perk3.getLore());
        }));

        List<Component> loreColor = List.of(
                Component.text("§7Vous pouvez rechangez la couleur de votre Nom!"),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA COULEUR")
        );
        inventory.put(15, new ItemBuilder(this, ColorUtils.getMaterialFromColor(mayorElector.getElectorColor()), itemMeta -> {
            itemMeta.itemName(Component.text("§7Changer votre ").append(Component.text("couleur").color(mayorElector.getElectorColor())));
            itemMeta.lore(loreColor);
        }).setOnClick(inventoryClickEvent -> {
            new MayorColorMenu(player, null, null, "change").open();
        }));


        return inventory;
    }
}
