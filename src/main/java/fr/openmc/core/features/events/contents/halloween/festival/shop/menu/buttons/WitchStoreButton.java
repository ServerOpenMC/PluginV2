package fr.openmc.core.features.events.contents.halloween.festival.shop.menu.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchShopStoreMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Map;

public class WitchStoreButton {
    public static void init(Menu menu, Map<Integer, ItemMenuBuilder> contents, int[] slots) {
        Player player = menu.getOwner();

        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemMenuBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.events.halloween.festival.button.store.name"));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.events.halloween.festival.button.store.lore"
                    ));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    new WitchShopStoreMenu(player, 1).open();
                })
        );
    }
}
