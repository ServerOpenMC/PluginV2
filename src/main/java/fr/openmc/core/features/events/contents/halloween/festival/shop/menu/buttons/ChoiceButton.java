package fr.openmc.core.features.events.contents.halloween.festival.shop.menu.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchChoiceMenu;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchShopStoreMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Map;

public class ChoiceButton {
    public static void init(Menu menu, Map<Integer, ItemMenuBuilder> contents, int[] slots) {
        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemMenuBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.events.halloween.festival.button.house.name"));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.events.halloween.festival.button.house.lore"
                    ));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> new WitchChoiceMenu(menu.getOwner()).open())
        );
    }
}
