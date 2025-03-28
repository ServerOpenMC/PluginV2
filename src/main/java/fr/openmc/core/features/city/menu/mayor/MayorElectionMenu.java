package fr.openmc.core.features.city.menu.mayor;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.mayor.MayorElector;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.menu.CityTypeMenu;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MayorElectionMenu extends Menu {

    public MayorElectionMenu(Player owner) {
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
        City city = CityManager.getPlayerCity(player.getUniqueId());
        MayorManager mayorManager = MayorManager.getInstance();

        inventory.put(11, new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
            itemMeta.itemName(Component.text("§7Créer §dvotre ville"));
        }).setOnClick(inventoryClickEvent -> {

        }));

        // on regarde si le joueur s'est déjà présenter
        List<Component> loreCandidature;
        if (mayorManager.isPlayerElector(player)) {
            loreCandidature = List.of(
                    Component.text("§7Vous vous êtes déjà §5présenter §7!"),
                    Component.text("§7Modifier votre couleur et regardez les Réformes que vous avez choisis"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
            );
        } else {
            loreCandidature = List.of(
                    Component.text("§7Vous pouvez vous §5inscire §7afin d'être maire !"),
                    Component.text("§7Séléctionner vos Réformes et votre couleur !"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR VOUS INSCRIRE")
            );
        }

        inventory.put(15, new ItemBuilder(this, PlayerUtils.getPlayerSkull(player),itemMeta -> {
            itemMeta.itemName(Component.text("§7Votre §5Candidature"));
            itemMeta.lore(loreCandidature);
        }).setOnClick(inventoryClickEvent -> {
            if (mayorManager.isPlayerElector(player)) {
                //todo show perks selected and color
            } else {
               new MayorCreateMenu(player).open();
            }
        }));


        return inventory;
    }
}
