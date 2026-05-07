package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.menu.CityChunkMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class MapButton {
    private static boolean hasPermissionChunkSee;

    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();
        hasPermissionChunkSee = city.hasPermission(player.getUniqueId(), CityPermission.SEE_CHUNKS);

        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.main.map.title"));
                    itemMeta.lore(getDynamicLore(city));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    if (!hasPermissionChunkSee) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.menus.main.map.no_permission"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    new CityChunkMenu(player).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city) {
        List<Component> lore;
        if (hasPermissionChunkSee) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.map.lore.access",
                    Component.text(city.getChunks().size()).color(NamedTextColor.GREEN)
            );
        } else {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.map.lore.view",
                    Component.text(city.getChunks().size()).color(NamedTextColor.GREEN)
            );
        }
        return lore;
    }
}
