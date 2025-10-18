package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.rank.menus.CityRanksMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
                    itemMeta.displayName(Component.text("§6Grades de la Ville"));
                    itemMeta.lore(getDynamicLore(city, player));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
                        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + " !"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    new CityRanksMenu(player, city).open();
                })
        );
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        List<Component> lore;
        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
            lore = List.of(
                    Component.text("§7Gérer les grades de votre ville"),
                    Component.text("§7Votre Grade : §d" + city.getRankName(player.getUniqueId())),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX GRADES")
            );
        } else {
            lore = List.of(
                    Component.text("§7Gérer les grades de votre ville"),
                    Component.empty(),
                    Component.text("§cVous devez etre Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + " pour débloquer ceci")
            );
        }
        return lore;
    }
}
