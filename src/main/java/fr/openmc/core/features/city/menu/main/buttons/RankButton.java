package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.rank.menus.CityRanksMenu;
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

public class RankButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();

        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.city.menus.main.ranks.title"));
                    itemMeta.lore(getDynamicLore(city, player));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.havent_unlocked_feature", Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK))), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    new CityRanksMenu(player, city).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        List<Component> lore;
        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.ranks.lore.unlocked",
                    Component.text(city.getRankName(player.getUniqueId())).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.ranks.lore.locked",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK)).color(NamedTextColor.RED)
            );
        }
        return lore;
    }
}
