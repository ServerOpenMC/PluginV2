package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.city.sub.bank.menu.CityBankMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
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

public class BankButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();
        MenuUtils.createButtonItem(
                contents,
                slots,
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.main.bank.title"));
                    itemMeta.lore(getDynamicLore(city));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
                    if (cityCheck == null) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    if (!CityBankConditions.canOpenCityBank(cityCheck, player)) return;

                    new CityBankMenu(player).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city) {
        List<Component> lore;
        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CITY_BANK)) {
            lore = TranslationManager.translationLore("feature.city.menus.main.bank.lore.unlocked");
        } else {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.bank.lore.locked",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CITY_BANK)).color(NamedTextColor.RED)
            );
        }
        return lore;
    }
}
