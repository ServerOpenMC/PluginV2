package fr.openmc.core.features.city.menu.mayor.create;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mayor.MayorCandidate;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        return "Menu des Maires - Modification";
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

        try {
            MayorCandidate mayorCandidate = MayorManager.getInstance().getCandidate(player);
            Perks perk2 = PerkManager.getPerkById(mayorCandidate.getIdChoicePerk2());
            Perks perk3 = PerkManager.getPerkById(mayorCandidate.getIdChoicePerk3());

            assert perk2 != null;
            inventory.put(11, new ItemBuilder(this, perk2.getItemStack(), itemMeta -> {
                itemMeta.itemName(Component.text(perk2.getName()));
                itemMeta.lore(perk2.getLore());
            }));

            assert perk3 != null;
            inventory.put(13, new ItemBuilder(this, perk3.getItemStack(), itemMeta -> {
                itemMeta.itemName(Component.text(perk3.getName()));
                itemMeta.lore(perk3.getLore());
            }));

            List<Component> loreColor = List.of(
                    Component.text("§7Vous pouvez rechangez la couleur de votre Nom!"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA COULEUR")
            );
            inventory.put(15, new ItemBuilder(this, ColorUtils.getMaterialFromColor(mayorCandidate.getCandidateColor()), itemMeta -> {
                itemMeta.itemName(Component.text("§7Changer votre ").append(Component.text("couleur").color(mayorCandidate.getCandidateColor())));
                itemMeta.lore(loreColor);
            }).setOnClick(inventoryClickEvent -> {
                new MayorColorMenu(player, null, null, null, "change", null).open();
            }));

            inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
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

        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return inventory;
    }
}
