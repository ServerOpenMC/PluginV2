package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.menu.bank.BankMainMenu;
import fr.openmc.core.features.city.menu.playerlist.CityPlayerListMenu;
import fr.openmc.core.utils.PlayerUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityMenu extends Menu {

    public CityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CPermission.RENAME);
        boolean hasPermissionChest = city.hasPermission(player.getUniqueId(), CPermission.CHEST);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);
        boolean hasPermissionChunkSee = city.hasPermission(player.getUniqueId(), CPermission.SEE_CHUNKS);

        List<Component> loreModifyCity;

        if (hasPermissionRenameCity || hasPermissionOwner) {
            loreModifyCity = List.of(
                    Component.text("§7Maire de la Ville : " + Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()),
                    Component.text("§7Membre(s) : " + city.getMembers().size()),
                    Component.text("§e§lCLIQUEZ ICI POUR MODIFIER LA VILLE")
            );
        } else {
            loreModifyCity = List.of(
                    Component.text("§7Maire de la Ville : " + Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()),
                    Component.text("§7Membre(s) : " + city.getMembers().size())
            );
        }

        inventory.put(4, new ItemBuilder(this, Material.BOOKSHELF, itemMeta -> {
            itemMeta.itemName(Component.text("§d" + city.getCityName()));
            itemMeta.lore(loreModifyCity);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (hasPermissionOwner) {
                CityModifyMenu menu = new CityModifyMenu(player);
                menu.open();
            }
        }));

        inventory.put(8, new ItemBuilder(this, Material.DRAGON_EGG, itemMeta -> {
            itemMeta.itemName(Component.text("§cVotre Mascotte"));
            itemMeta.lore(List.of(
                    Component.text("§cVie : §7null/null")
            )); //TODO: Mascottes
        }));

        List<Component> loreChunkCity;

        if (hasPermissionChunkSee) {
            loreChunkCity = List.of(
                    Component.text("§7Votre ville a une superficie de §6" + city.getChunks().size()),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER A LA CARTE")
            );
        } else {
            loreChunkCity = List.of(
                    Component.text("§7Votre ville a une superficie de §6" + city.getChunks().size())
            );
        }

        inventory.put(19, new ItemBuilder(this, Material.OAK_FENCE, itemMeta -> {
            itemMeta.itemName(Component.text("§6Taille de votre Ville"));
            itemMeta.lore(loreChunkCity);
        }).setOnClick(inventoryClickEvent -> {
            if (!hasPermissionChunkSee) {
                MessagesManager.sendMessageType(player, Component.text("Vous n'avez pas les permissions de voir les claims"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityChunkMenu menu = new CityChunkMenu(player);
            menu.open();
        }));

        ItemStack playerHead = PlayerUtils.getPlayerSkull(player);

        inventory.put(22, new ItemBuilder(this, playerHead, itemMeta -> {
            itemMeta.displayName(Component.text("§dListe des Membres"));
            itemMeta.lore(List.of(
                    Component.text("§7Il y a actuellement §d" + city.getMembers().size() + "§7 membre(s) dans votre ville")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityPlayerListMenu menu = new CityPlayerListMenu(player);
            menu.open();
        }));

        inventory.put(25, new ItemBuilder(this, Material.NETHERITE_SWORD, itemMeta -> {
            itemMeta.itemName(Component.text("§5Le Statut de votre Ville"));
            itemMeta.lore(List.of(
                    Component.text("§7Votre ville est en ...") //TODO: Systeme de Status des Villes (voir cdc, en paix, en guerre, commerce)
            ));
        }));

        List<Component> loreChestCity;

        if (hasPermissionChest) {
            loreChestCity = List.of(
                    Component.text("§7Acceder au Coffre de votre Ville pour"),
                    Component.text("§7stocker des items en commun"),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU COFFRE")
            );
        } else {
            loreChestCity = List.of(
                    Component.text("§7Vous n'avez pas le §cdroit de visionner le coffre !")
            );
        }

        inventory.put(36, new ItemBuilder(this, Material.CHEST, itemMeta -> {
            itemMeta.itemName(Component.text("§aLe Coffre de la Ville"));
            itemMeta.lore(loreChestCity);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (!hasPermissionChest) {
                MessagesManager.sendMessageType(player, Component.text("Vous n'avez pas les permissions de voir le coffre"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (city.getChestWatcher() != null) {
                MessagesManager.sendMessageType(player, Component.text("Le coffre est déjà ouvert"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            new ChestMenu(city, 1).open(player);
        }));

        inventory.put(40, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.itemName(Component.text("§6La Banque"));
            itemMeta.lore(List.of(
                    Component.text("§7Stocker votre argent et celle de votre ville"),
                    Component.text("§7Contribuer au développement de votre ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX COMPTES")
            ));
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            BankMainMenu menu = new BankMainMenu(player);
            menu.open();
        }));


        if (!hasPermissionOwner) {
            inventory.put(44, new ItemBuilder(this, Material.OAK_DOOR, itemMeta -> {
                itemMeta.itemName(Component.text("§cPartir de la Ville"));
                itemMeta.lore(List.of(
                        Component.text("§7Vous allez §cquitter §7" + city.getCityName()),
                        Component.text("§e§lCLIQUEZ ICI POUR PARTIR")
                ));
            }).setOnClick(inventoryClickEvent -> {
                City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
                if (cityCheck == null) {
                    MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    return;
                }

                ConfirmMenu menu = new ConfirmMenu(player,
                        () -> {
                            CityCommands.leaveCity(player);
                            player.closeInventory();
                        },
                        () -> {
                            player.closeInventory();
                        },
                        "§7Voulez vous vraiment partir de " + city.getCityName() + " ?",
                        "§7Rester dans la ville " + city.getCityName()
                );
                menu.open();
            }));
        }

        return inventory;
    }
}