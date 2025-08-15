package fr.openmc.core.features.city.menu.playerlist;

import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityKickAction;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.menu.CitizensPermsMenu;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.InputUtils;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH_PLAYERNAME;

public class CityPlayerListMenu extends PaginatedMenu {

    public CityPlayerListMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionKick = city.hasPermission(player.getUniqueId(), CPermission.KICK);
        boolean hasPermissionPerms = city.hasPermission(player.getUniqueId(), CPermission.PERMS);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

        for (UUID uuid : city.getMembers()) {
            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

            String title = city.getRankName(uuid) + " ";

            List<Component> lorePlayer;
            if (city.hasPermission(playerOffline.getUniqueId(), CPermission.OWNER)) {
                lorePlayer = List.of(
                        Component.text("§7Le priopriétaire de la ville.")
                );
            } else if (hasPermissionPerms && hasPermissionKick) {
                if (city.hasPermission(playerOffline.getUniqueId(), CPermission.OWNER)) {
                    lorePlayer = List.of(
                            Component.text("§7Vous ne pouvez pas éditer le propriétaire!")
                    );
                } else {
                    lorePlayer = List.of(
                            Component.text("§7Vous pouvez gérer ce joueur comme l'§cexpulser §7ou bien modifier §ases permissions"),
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

            List<Component> finalLorePlayer = lorePlayer;
            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
                itemMeta.displayName(Component.text(title + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
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
                                    CityKickAction.startKick(player, playerOffline);
                                },
                                () -> player.closeInventory(),
                                List.of(Component.text("§7Voulez vous vraiment expulser " + playerOffline.getName() + " ?")),
                                List.of(Component.text("§7Ne pas expulser " + playerOffline.getName())));
                        menu.open();

                    }
                }
            }));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Player player = getOwner();
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_cancel")).getBest(), itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au menu précédent"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }, true));
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
        map.put(53, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_search")).getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§7Inviter des §dpersonnes"));
            itemMeta.lore(List.of(Component.text("§7Vous pouvez inviter des personnes à votre ville pour la remplir !")));
        }).setOnClick(inventoryClickEvent -> {
            DialogInput.send(player, Component.text("Entrez le nom du joueur"), MAX_LENGTH_PLAYERNAME, input -> {
                if (InputUtils.isInputPlayer(input)) {
                    Player playerToInvite = Bukkit.getPlayer(input);
                    CityCommands.invite(player, playerToInvite);
                } else {
                    MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
                }
            });
        }));
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Membres";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
