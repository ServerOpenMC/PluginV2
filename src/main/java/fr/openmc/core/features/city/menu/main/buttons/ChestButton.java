package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.conditions.CityChestConditions;
import fr.openmc.core.features.city.menu.CityChestMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ChestButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();
        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.main.chest.title"));
                    itemMeta.lore(getDynamicLore(city, player));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

                    if (!CityChestConditions.canCityChestOpen(cityCheck, player)) return;

                    new CityChestMenu(player, city, 1).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        boolean hasPermissionChest = city.hasPermission(player.getUniqueId(), CityPermission.ACCESS_CITY_CHEST);
        List<Component> lore;
        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CHEST)) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.chest.lore.locked",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CHEST)).color(NamedTextColor.RED)
            );
        } else {
            if (hasPermissionChest) {
                if (city.getChestWatcher() != null) {
                    lore = TranslationManager.translationLore(
                            "feature.city.menus.main.chest.lore.opened",
                            Component.text(PlayerNameCache.getName(city.getChestWatcher())).color(NamedTextColor.RED)
                    );
                } else {
                    lore = TranslationManager.translationLore("feature.city.menus.main.chest.lore.click");
                }
            } else {
                lore = TranslationManager.translationLore("feature.city.menus.main.chest.lore.no_permission");
            }
        }
        return lore;
    }
}
