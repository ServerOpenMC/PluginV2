package fr.openmc.core.features.city.sub.mayor.menu.create;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorCandidate;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorModifyMenu extends Menu {
    public MayorModifyMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemsAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-38%%img_mayor%");
        } else {
            return "Menu des Maires - Modification";
        }
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        MayorCandidate mayorCandidate = MayorManager.getCandidate(player.getUniqueId());
        Perks perk2 = PerkManager.getPerkById(mayorCandidate.getIdChoicePerk2());
        Perks perk3 = PerkManager.getPerkById(mayorCandidate.getIdChoicePerk3());

        assert perk2 != null;
        inventory.put(20, new ItemBuilder(this, perk2.getItemStack(), itemMeta -> {
            itemMeta.customName(Component.text(perk2.getName()));
            itemMeta.lore(perk2.getLore());
        }));

        assert perk3 != null;
        inventory.put(22, new ItemBuilder(this, perk3.getItemStack(), itemMeta -> {
            itemMeta.customName(Component.text(perk3.getName()));
            itemMeta.lore(perk3.getLore());
        }));

        List<Component> loreColor = List.of(
                Component.text("§7Vous pouvez rechangez la couleur de votre Nom!"),
                Component.empty(),
                Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA COULEUR")
        );
        inventory.put(24, new ItemBuilder(this, ColorUtils.getMaterialFromColor(mayorCandidate.getCandidateColor()), itemMeta -> {
            itemMeta.itemName(Component.text("§7Changer votre ").append(Component.text("couleur").color(mayorCandidate.getCandidateColor())));
            itemMeta.lore(loreColor);
        }).setOnClick(inventoryClickEvent -> {
            new MayorColorMenu(player, null, null, null, "change", null).open();
        }));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de votre ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
        }));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
