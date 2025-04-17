package fr.openmc.core.features.city.menu;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CityModifyMenu extends Menu {

    public CityModifyMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Modifier";
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

        boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CPermission.RENAME);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);


        List<Component> loreRename;

        if (hasPermissionRenameCity) {
            loreRename = List.of(
                    Component.text("§7Vous pouvez renommer votre §dville§7."),
                    Component.text(""),
                    Component.text("§7Nom actuel : §d" + city.getCityName()),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR LE MODIFIER")
            );
        } else {
            loreRename = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Renommer votre §dville"));
            itemMeta.lore(loreRename);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (!CityManageConditions.canCityRename(cityCheck, player)) return;

            String[] lines = new String[4];
            lines[0] = "";
            lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
            lines[2] = "Entrez votre";
            lines[3] = "nom ci dessus";

            SignGUI gui;
            try {
                gui = SignGUI.builder()
                        .setLines(null, lines[1], lines[2], lines[3])
                        .setType(ItemUtils.getSignType(player))
                        .setHandler((p, result) -> {
                            String input = result.getLine(0);

                            if (InputUtils.isInputCityName(input)) {
                                City playerCity = CityManager.getPlayerCity(player.getUniqueId());
	                            
	                            assert playerCity != null;
	                            playerCity.renameCity(input);
                                MessagesManager.sendMessage(player, Component.text("La ville a été renommée en " + input), Prefix.CITY, MessageType.SUCCESS, false);

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


        List<Component> loreTransfer;

        if (hasPermissionOwner) {
            loreTransfer = List.of(
                    Component.text("§dLa Ville §7sera transferer à §dla personne §7que vous séléctionnerez"),
                    Component.text("§e§lCLIQUEZ ICI POUR CHOISIR")
            );
        } else {
            loreTransfer = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(13, new ItemBuilder(this, Material.TOTEM_OF_UNDYING, itemMeta -> {
            itemMeta.itemName(Component.text("§7Transferer la §dVille"));
            itemMeta.lore(loreTransfer);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

            if (!CityManageConditions.canCityTransfer(cityCheck, player)) return;

            if (city.getMembers().size() - 1 == 0) {
                MessagesManager.sendMessage(player, Component.text("Il y a pas de membre a qui vous pouvez transferer la ville"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityTransferMenu menu = new CityTransferMenu(player);
            menu.open();

        }));


        List<Component> loreDelete;

        if (hasPermissionOwner) {
            loreDelete = List.of(
                    Component.text("§7Vous allez définitivement §csupprimer la ville!"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            );
        } else {
            loreDelete = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.TNT, itemMeta -> {
            itemMeta.itemName(Component.text("§7Supprimer la ville"));
            itemMeta.lore(loreDelete);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

            if (!CityManageConditions.canCityDelete(city, player)) return;
	        
	        assert cityCheck != null;
	        ConfirmMenu menu = new ConfirmMenu(
                    player,
                    () -> {
                        player.closeInventory();
                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> CityCommands.deleteCity(player));
                    },
			        player::closeInventory,
                    List.of(Component.text("§7Voulez vous vraiment dissoudre la ville " + cityCheck.getCityName() + " ?")),
                    List.of(Component.text("§7Ne pas dissoudre la ville " + cityCheck.getCityName())));
            menu.open();

        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de votre Ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityMenu menu = new CityMenu(player);
            menu.open();
        }));

        return inventory;
    }
}