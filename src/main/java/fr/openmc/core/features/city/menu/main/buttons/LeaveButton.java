package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityLeaveAction;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class LeaveButton {
    private static boolean hasPermissionOwner;

    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();
        hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.main.leave.title"));
                    itemMeta.lore(getDynamicLore(city));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    if (hasPermissionOwner) return;

                    City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
                    if (!CityLeaveCondition.canCityLeave(cityCheck, player)) return;

                    new ConfirmMenu(player,
                            () -> {
                                CityLeaveAction.startLeave(player);
                                player.closeInventory();
                            },
                            player::closeInventory,
                            List.of(TranslationManager.translation("feature.city.menus.main.leave.confirm.accept", Component.text(city.getName()).color(NamedTextColor.GRAY))),
                            List.of(TranslationManager.translation("feature.city.menus.main.leave.confirm.deny", Component.text(city.getName()).color(NamedTextColor.GRAY)))
                    ).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city) {
        List<Component> lore;
        if (!hasPermissionOwner) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.leave.lore.leave",
                    Component.text(city.getName()).color(NamedTextColor.GRAY)
            );
        } else {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.leave.lore.owner_cant",
                    Component.text(city.getName()).color(NamedTextColor.GRAY)
            );
        }
        return lore;
    }
}
