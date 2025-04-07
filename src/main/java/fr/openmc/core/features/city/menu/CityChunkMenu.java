package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.commands.CityCommands.calculateAywenite;
import static fr.openmc.core.features.city.commands.CityCommands.calculatePrice;

public class CityChunkMenu extends Menu {

    public CityChunkMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Ville - La Carte";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        City city2 = CityManager.getPlayerCity(player.getUniqueId());

        boolean hasPermissionClaim = city2.hasPermission(player.getUniqueId(), CPermission.CLAIM);

        int playerChunkX = player.getLocation().getChunk().getX();
        int playerChunkZ = player.getLocation().getChunk().getZ();

        int startX = playerChunkX - 4;
        int startZ = playerChunkZ - 2;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int chunkX = startX + col;
                int chunkZ = startZ + row;

                City city = CityManager.getCityFromChunk(chunkX, chunkZ);
                ItemStack chunkItem;
                Material material = null;

                if (chunkX == playerChunkX && chunkZ == playerChunkZ) {
                    material=Material.LIME_STAINED_GLASS_PANE;
                }

                if (city != null) {
                    if (CityManager.getPlayerCity(player.getUniqueId()).getUUID().equals(city.getUUID())) {
                        if (material == null) material = Material.BLUE_STAINED_GLASS_PANE;
                        chunkItem = new ItemBuilder(this, material, itemMeta -> {
                            itemMeta.displayName(Component.text("§9Claim de votre ville"));
                            itemMeta.lore(List.of(
                                    Component.text("§7Ville : §d" + city.getCityName()),
                                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ)
                            ));
                        });
                    } else {
                        if (material == null) material = Material.RED_STAINED_GLASS_PANE;
                        chunkItem = new ItemBuilder(this, material, itemMeta -> {
                            itemMeta.displayName(Component.text("§cClaim d'une ville adverse"));
                            itemMeta.lore(List.of(
                                    Component.text("§7Ville : §d" + city.getCityName()),
                                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ)
                            ));
                        });
                    }
                } else {
                    if (material == null) material = Material.GRAY_STAINED_GLASS_PANE;
                    int nbChunk = city2.getChunks().size();
                    List<Component> listComponent;

                    if (MascotsManager.freeClaim.containsKey(city2.getUUID()) && MascotsManager.freeClaim.get(city2.getUUID())>0) {
                        listComponent = List.of(
                                Component.text("§7Position : §f" + chunkX + ", " + chunkZ),
                                Component.text(""),
                                Component.text("§cCoûte :"),
                                Component.text("§8- §6Claim Gratuit"),
                                Component.text(""),
                                Component.text("§e§lCLIQUEZ POUR CLAIM")
                        );
                    } else {
                        listComponent = List.of(
                                Component.text("§7Position : §f" + chunkX + ", " + chunkZ),
                                Component.text(""),
                                Component.text("§cCoûte :"),
                                Component.text("§8- §6"+ (double) calculatePrice(nbChunk)).append(Component.text(EconomyManager.getEconomyIcon())).decoration(TextDecoration.ITALIC, false),
                                Component.text("§8- §d"+ calculateAywenite(nbChunk) + " d'Aywenite"),
                                Component.text(""),
                                Component.text("§e§lCLIQUEZ POUR CLAIM")
                        );
                    }

                    chunkItem = new ItemBuilder(this, material, itemMeta -> {
                        itemMeta.displayName(Component.text("§cClaim libre"));
                        itemMeta.lore(listComponent);
                    }).setOnClick(inventoryClickEvent -> {
                        City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

                        if (cityCheck == null) {
                            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                            return;
                        }

                        if (!hasPermissionClaim) {
                            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCLAIM.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                            return;
                        }


                        ConfirmMenu menu = new ConfirmMenu(
                                player,
                                () -> {
                                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                                        CityCommands.claim(player, chunkX, chunkZ);
                                    });
                                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                                        new CityChunkMenu(player).open();
                                    }, 2);
                                },
                                () -> {
                                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                                        new CityChunkMenu(player).open();
                                    }, 2);
                                },
                                List.of(Component.text("§7Voulez vous vraiment claim ce chunk ?")),
                                List.of(Component.text("§7Annuler la procédure de claim")));
                        menu.open();

                    });
                }

                inventory.put(row * 9 + col, chunkItem);
            }
        }

        inventory.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
            itemMeta.lore(List.of(Component.text("§7Retourner au menu des villes")));
        }).setOnClick(event -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
        }));

        if (MascotsManager.freeClaim.containsKey(city2.getUUID()) && MascotsManager.freeClaim.get(city2.getUUID())>0) {
            inventory.put(49, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
                itemMeta.displayName(Component.text("§6Claim Gratuit"));
                itemMeta.lore(List.of(Component.text("§7Vous avez §6" + MascotsManager.freeClaim.get(city2.getUUID())+ " claim gratuit !")));
            }));
        }

        inventory.put(53, new ItemBuilder(this, Material.MAP, itemMeta -> {
            itemMeta.displayName(Component.text("§6Rafraîchir la carte"));
            itemMeta.lore(List.of(Component.text("§7Mettre à jour §6les claims affichés§7.")));
        }).setOnClick(event -> {
            CityChunkMenu newMenu = new CityChunkMenu(player);
            newMenu.open();
        }));

        return inventory;
    }
}