package fr.openmc.core.features.events.contents.halloween.halloween.shop.menu.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Map;

public class WitchHouseButton {
    public static void init(Menu menu, Map<Integer, ItemMenuBuilder> contents, int[] slots) {
        Player player = menu.getOwner();

        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemMenuBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.city.menus.main.ranks.title"));//TODO
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.city.menus.main.ranks.lore.unlocked", //TODO
                            Component.text("").color(NamedTextColor.LIGHT_PURPLE)
                    ));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setCloseButton()//.setOnClick(inventoryClickEvent -> { //TODO tp dans la dimension
//                    inventoryClickEvent.getClickedInventory().close();
//                })
        );
    }
}
