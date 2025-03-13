package fr.openmc.core.features.city.menu.playerlist;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.conditions.CityKickCondition;
import fr.openmc.core.features.city.menu.CitizensPermsMenu;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityPlayerGestionMenu extends Menu {

    private final OfflinePlayer playerTarget;
    public CityPlayerGestionMenu(Player owner, OfflinePlayer player) {
        super(owner);
        this.playerTarget=player;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Modifier un Joueur";
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

        boolean hasPermissionKick = city.hasPermission(player.getUniqueId(), CPermission.KICK);
        boolean hasPermissionPerms = city.hasPermission(player.getUniqueId(), CPermission.PERMS);


        List<Component> loreKick;

        if (hasPermissionKick) {
            if (player.getUniqueId().equals(playerTarget.getUniqueId())) {
                loreKick = List.of(
                        Component.text("§cVous pouvez pas vous expluser")
                );
            } else if (city.hasPermission(playerTarget.getUniqueId(), CPermission.OWNER)) {
                loreKick = List.of(
                        Component.text("§cVous pouvez pas expluser le propriétaire")
                );
            } else {
                loreKick = List.of(
                        Component.text("§7Vous pouvez expluser" + playerTarget.getName() + "§7de votre §dville§7."),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR L'EXPLUSER")
                );
            }
        } else {
            loreKick = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.OAK_DOOR, itemMeta -> {
            itemMeta.itemName(Component.text("§cExpluser " + playerTarget.getName()));
            itemMeta.lore(loreKick);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityKickCondition.canCityKickPlayer(city, player, playerTarget)) {
                return;
            } else {
                ConfirmMenu menu = new ConfirmMenu(
                        player,
                        () -> {
                            player.closeInventory();
                            CityCommands.kick(player, playerTarget);
                        },
                        () -> player.closeInventory(),
                        List.of(Component.text("§7Voulez vous vraiment expluser " + playerTarget.getName() + " ?")),
                        List.of(Component.text("§7Ne pas expluser " + playerTarget.getName())));
                menu.open();

            }
        }));


        List<Component> lorePlayerTarget = List.of(
                    Component.text("§7Vous êtes entrain de modifier son status dans la §dville")
        );

        inventory.put(13, new ItemBuilder(this, ItemUtils.getPlayerSkull(playerTarget.getUniqueId()), itemMeta -> {
            itemMeta.displayName(Component.text("§eJoueur " + playerTarget.getName()));
            itemMeta.lore(lorePlayerTarget);
        }));

        List<Component> lorePermission;

        if (hasPermissionPerms) {
            lorePermission = List.of(
                    Component.text("§7Vous allez modifier §ases permisisons"),
                    Component.text("§e§lCLIQUEZ ICI POUR MODIFIER")
            );
        } else {
            lorePermission = List.of(
                    MessagesManager.Message.NOPERMISSION2.getMessage()
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.itemName(Component.text("§cModifier les permissions"));
            itemMeta.lore(lorePermission);
        }).setOnClick(inventoryClickEvent -> {
            CitizensPermsMenu.openBookFor(player, playerTarget.getUniqueId());
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

            CityPlayerListMenu menu = new CityPlayerListMenu(player);
            menu.open();
        }));

        return inventory;
    }
}
