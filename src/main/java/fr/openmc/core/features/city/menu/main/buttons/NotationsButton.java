package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.menu.NotationDialog;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class NotationsButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();
        CityNotation notation = city.getNotationOfWeek(DateUtils.getWeekFormat());

        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.main.notation.title"));
                    itemMeta.lore(getDynamicLore(city, notation));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.NOTATION) && notation != null) {
                        NotationDialog.send(player, DateUtils.getWeekFormat());
                    }
                })
        );
    }

    private static List<Component> getDynamicLore(City city, CityNotation notation) {
        List<Component> lore;
        if (notation != null) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.notation.lore",
                    Component.text(Math.floor(notation.getTotalNote())).color(NamedTextColor.BLUE),
                    Component.text(NotationNote.getMaxTotalNote()).color(NamedTextColor.BLUE),
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(notation.getMoney())).color(NamedTextColor.GOLD),
                    Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD)
            );
        } else {
            if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.NOTATION)) {
                lore = TranslationManager.translationLore("feature.city.menus.main.notation.lore.none");
            } else {
                lore = TranslationManager.translationLore(
                        "feature.city.menus.main.notation.lore.locked",
                        Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.NOTATION)).color(NamedTextColor.RED)
                );
            }
        }
        return lore;
    }
}
