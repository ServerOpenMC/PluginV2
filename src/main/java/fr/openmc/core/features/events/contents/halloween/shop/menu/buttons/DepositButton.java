package fr.openmc.core.features.events.contents.halloween.shop.menu.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.events.contents.halloween.witchhouse.advancement.menu.AdvancementMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Map;

public class DepositButton {
    public static void init(Menu menu, Map<Integer, ItemMenuBuilder> contents, int[] slots) {
        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemMenuBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.events.halloween.festival.button.deposit.name"));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.events.halloween.festival.button.deposit.lore"
                    ));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> new AdvancementMenu(menu.getOwner()).open())
        );
    }
}
