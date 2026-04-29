package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.menu.CityChunkMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
                    itemMeta.itemName(Component.text("§aTaille de votre ville"));
                    itemMeta.lore(getDynamicLore(city));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    if (!hasPermissionChunkSee) {
                        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions de voir les claims"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    new CityChunkMenu(player).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city) {
        List<Component> lore;
        if (hasPermissionChunkSee) {
            lore = List.of(
                    Component.text("§7Votre ville a une superficie de §a" + city.getChunks().size()),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER A LA CARTE")
            );
        } else {
            lore = List.of(
                    Component.text("§7Votre ville a une superficie de §a" + city.getChunks().size())
            );
        }
        return lore;
    }
}
