package fr.openmc.core.features.city.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityDeleteAction;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.utils.text.InputUtils.MAX_LENGTH_CITY;

public class CityModifyMenu extends Menu {

    public CityModifyMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.modify.name");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
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

        boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CityPermission.RENAME);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);


        List<Component> loreRename;

        if (hasPermissionRenameCity) {
            loreRename = TranslationManager.translationLore(
                    "feature.city.menus.modify.rename.lore",
                    Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            loreRename = List.of(
                    TranslationManager.translation("messages.global.cannot_do_this")
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.menus.modify.rename.title"));
            itemMeta.lore(loreRename);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (!CityManageConditions.canCityRename(cityCheck, player)) return;

            DialogInput.send(player, TranslationManager.translation("feature.city.commands.create.enter_city_name"), MAX_LENGTH_CITY, input -> {
                if (input == null) return;
                if (InputUtils.isInputCityName(input)) {
                    City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                    playerCity.rename(input);
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.commands.rename.success", Component.text(input)), Prefix.CITY, MessageType.SUCCESS, false);

                } else {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.menus.modify.rename.invalid"), Prefix.CITY, MessageType.ERROR, true);
                }
            });

        }));


        List<Component> loreTransfer;

        if (hasPermissionOwner) {
            loreTransfer = TranslationManager.translationLore("feature.city.menus.modify.transfer.lore");
        } else {
            loreTransfer = List.of(
                    TranslationManager.translation("messages.global.cannot_do_this")
            );
        }

        inventory.put(13, new ItemBuilder(this, Material.TOTEM_OF_UNDYING, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.menus.modify.transfer.title"));
            itemMeta.lore(loreTransfer);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

            if (!CityManageConditions.canCityTransfer(cityCheck, player)) return;

            if (city.getMembers().size() - 1 == 0) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.menus.modify.transfer.no_member"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityTransferMenu menu = new CityTransferMenu(player);
            menu.open();

        }));

        Supplier<ItemBuilder> deleteItemSupplier = () -> {
                List<Component> loreDelete;
                if (hasPermissionOwner) {
                    if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
                        loreDelete = TranslationManager.translationLore(
                                "feature.city.menus.modify.delete.lore.wait",
                                Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big"))).color(NamedTextColor.RED)
                        );
                    } else {
                        loreDelete = TranslationManager.translationLore("feature.city.menus.modify.delete.lore.click");
                    }
                } else {
                    loreDelete = List.of(
                            TranslationManager.translation("messages.global.cannot_do_this")
                    );
                }
                return new ItemBuilder(this, Material.TNT, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.modify.delete.title"));
                    itemMeta.lore(loreDelete);
                }).setOnClick(inventoryClickEvent -> {
                    CityDeleteAction.startDeleteCity(player);
                });
            };

            if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
                MenuUtils.runDynamicItem(player, this, 15, deleteItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                inventory.put(15, deleteItemSupplier.get());
            }

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
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
