package fr.openmc.core.features.city.menu.playerlist;


import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.menu.CitizensPermsMenu;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityPlayerListMenu extends PaginatedMenu {

    public CityPlayerListMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionKick = city.hasPermission(player.getUniqueId(), CPermission.KICK);
        boolean hasPermissionPerms = city.hasPermission(player.getUniqueId(), CPermission.PERMS);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

        List<ItemStack> items = new ArrayList<>();
        for (UUID uuid : city.getMembers()) {
            OfflinePlayer playerOffline = Bukkit.getOfflinePlayer(uuid);
            String title = "";
            if(hasPermissionOwner) {
                title = "Propriétaire ";
            } else {
                title = "Membre ";
            }

            List<Component> lorePlayer = List.of();
            if (hasPermissionPerms && hasPermissionKick) {
                if (city.hasPermission(playerOffline.getUniqueId(), CPermission.OWNER)) {
                    lorePlayer = List.of(
                        Component.text("§7Vous ne pouvez pas éditer le propriétaire!")
                    );
                } else {
                    lorePlayer = List.of(
                            Component.text("§7Vous pouvez gérer ce joueur comme l'§cexpluser §7ou bien modifier §ases permissions"),
                            Component.text("§e§lCLIQUEZ ICI POUR GERER CE JOUEUR")
                    );
                }
            } else if (hasPermissionPerms) {
                lorePlayer = List.of(
                        Component.text("§7Vous pouvez modifier les permissions de ce joueur"),
                        Component.text("§e§lCLIQUEZ ICI POUR MODIFIER SES PERMISSIONS")
                );
            } else if (hasPermissionKick) {
                if (player.getUniqueId().equals(playerOffline.getUniqueId())) {
                    lorePlayer = List.of(
                            Component.text("§7Vous ne pouvez pas vous §aexclure §7vous même!")
                    );
                } else if (city.hasPermission(playerOffline.getUniqueId(), CPermission.OWNER)) {
                    lorePlayer = List.of(
                            Component.text("§7Vous ne pouvez pas §aexclure §7le propriétaire!")
                    );
                } else {
                    lorePlayer = List.of(
                            Component.text("§7Vous pouvez exclure ce joueur"),
                            Component.text("§e§lCLIQUEZ ICI POUR L'EXCLURE")
                    );
                }
            } else {
                lorePlayer = List.of(
                        Component.text("§7Un membre comme vous.")
                );
            }

            String finalTitle = title;
            List<Component> finalLorePlayer = lorePlayer;
            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
                itemMeta.displayName(Component.text(finalTitle + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(finalLorePlayer);
            }).setOnClick(inventoryClickEvent -> {
                if (city.hasPermission(playerOffline.getUniqueId(), CPermission.OWNER)) {
                    return;
                }
                if (hasPermissionPerms && hasPermissionKick) {
                    CityPlayerGestionMenu menu = new CityPlayerGestionMenu(player, playerOffline);
                    menu.open();
                } else if (hasPermissionPerms) {
                    CitizensPermsMenu.openBookFor(player, playerOffline.getUniqueId());
                } else if (hasPermissionKick) {
                    if (player.getUniqueId().equals(playerOffline.getUniqueId())) {
                        return;
                    } else if (city.hasPermission(playerOffline.getUniqueId(), CPermission.OWNER)) {
                        return;
                    } else {
                        ConfirmMenu menu = new ConfirmMenu(
                                player,
                                () -> {
                                    player.closeInventory();
                                    CityCommands.kick(player, playerOffline);
                                },
                                () -> player.closeInventory(),
                                List.of(Component.text("§7Voulez vous vraiment expluser " + playerOffline.getName() + " ?")),
                                List.of(Component.text( "§7Ne pas expluser " + playerOffline.getName())));
                        menu.open();

                    }
                }
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Player player = getOwner();
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§7Fermer"));
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cPage précédente"));
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("menu:next_page").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
        }).setNextPageButton());
        map.put(53, new ItemBuilder(this, CustomItemRegistry.getByName("menu:search_btn").getBest(),itemMeta -> {
            itemMeta.displayName(Component.text("§7Inviter des §dpersonnes"));
            itemMeta.lore(List.of(Component.text("§7Vous pouvez inviter des personnes à votre ville pour la remplir !")));
        }).setOnClick(inventoryClickEvent -> {
            String[] lines = new String[4];
            lines[0] = "";
            lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
            lines[2] = "Entrez le nom du ";
            lines[3] = "joueur ci dessus";

            SignGUI gui = null;
            try {
                gui = SignGUI.builder()
                        .setLines(null, lines[1] , lines[2], lines[3])
                        .setType(fr.openmc.core.utils.ItemUtils.getSignType(player))
                        .setHandler((p, result) -> {
                            String input = result.getLine(0);

                            if (InputUtils.isInputPlayer(input)) {
                                Player playerToInvite = Bukkit.getPlayer(input);
                                CityCommands.add(player, playerToInvite);
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
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Membres";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }
}
