package fr.openmc.core.features.city.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityDeleteAction;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH_CITY;

public class CityModifyMenu extends Menu {

    public CityModifyMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Modifier";
    }

    @Override
    public String getTexture() {
        return null;
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CPermission.RENAME);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);


        List<Component> loreRename;

        if (hasPermissionRenameCity) {
            loreRename = List.of(
                    Component.text("§7Vous pouvez renommer votre §dville§7."),
                    Component.empty(),
                    Component.text("§7Nom actuel : §d" + city.getName()),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR LE MODIFIER")
            );
        } else {
            loreRename = List.of(
                    MessagesManager.Message.NO_PERMISSION_2.getMessage()
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(Component.text("§7Renommer votre §dville"));
            itemMeta.lore(loreRename);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (!CityManageConditions.canCityRename(cityCheck, player)) return;

            DialogInput.send(player, Component.text("Entrez le nom de la ville"), MAX_LENGTH_CITY, input -> {
                if (InputUtils.isInputCityName(input)) {
                    City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                    playerCity.rename(input);
                    MessagesManager.sendMessage(player, Component.text("La ville a été renommée en " + input), Prefix.CITY, MessageType.SUCCESS, false);

                } else {
                    MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
                }
            });

        }));


        List<Component> loreTransfer;

        if (hasPermissionOwner) {
            loreTransfer = List.of(
                    Component.text("§dLa Ville §7sera transferer à §dla personne §7que vous séléctionnerez"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR CHOISIR")
            );
        } else {
            loreTransfer = List.of(
                    MessagesManager.Message.NO_PERMISSION_2.getMessage()
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

        Supplier<ItemBuilder> deleteItemSupplier = () -> {
                List<Component> loreDelete;
                if (hasPermissionOwner) {
                    if (!DynamicCooldownManager.isReady(player.getUniqueId().toString(), "city:big")) {
                        loreDelete = List.of(
                                Component.text("§7Vous allez définitivement §csupprimer la ville!"),
                                Component.empty(),
                                Component.text("§7Vous devez attendre §c" + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(player.getUniqueId().toString(), "city:big")) + " §7avant de pouvoir delete votre ville")
                        );
                    } else {
                        loreDelete = List.of(
                                Component.text("§7Vous allez définitivement §csupprimer la ville!"),
                                Component.empty(),
                                Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                        );
                    }
                } else {
                    loreDelete = List.of(
                            MessagesManager.Message.NO_PERMISSION_2.getMessage()
                    );
                }
                return new ItemBuilder(this, Material.TNT, itemMeta -> {
                    itemMeta.itemName(Component.text("§7Supprimer la ville"));
                    itemMeta.lore(loreDelete);
                }).setOnClick(inventoryClickEvent -> {
                    CityDeleteAction.startDeleteCity(player);
                });
            };

            if (!DynamicCooldownManager.isReady(player.getUniqueId().toString(), "city:big")) {
                MenuUtils.runDynamicItem(player, this, 15, deleteItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                inventory.put(15, deleteItemSupplier.get());
            }

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§e§lCLIQUEZ ICI POUR RETOURNER")
            ));
        }, true));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
