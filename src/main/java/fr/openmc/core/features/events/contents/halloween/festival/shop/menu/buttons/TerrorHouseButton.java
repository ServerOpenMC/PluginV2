package fr.openmc.core.features.events.contents.halloween.festival.shop.menu.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Map;

public class TerrorHouseButton {
    public static void init(Menu menu, Map<Integer, ItemMenuBuilder> contents, int[] slots) {
        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemMenuBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.events.halloween.festival.button.terror_house.name"));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.events.halloween.festival.button.terror_house.lore"
                    ));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setCloseButton()//.setOnClick(inventoryClickEvent -> { //TODO tp dans la dimension
//                    inventoryClickEvent.getClickedInventory().close();
//                })
        );
    }
}
