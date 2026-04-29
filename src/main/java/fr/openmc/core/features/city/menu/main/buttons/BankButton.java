package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.city.sub.bank.menu.CityBankMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
                    itemMeta.itemName(Component.text("§6La banque"));
                    itemMeta.lore(getDynamicLore(city));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> {
                    City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
                    if (cityCheck == null) {
                        MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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
            lore = List.of(
                    Component.text("§7Stocker votre argent et celle de votre ville"),
                    Component.text("§7Contribuer au développement de votre ville"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX COMPTES")
            );
        } else {
            lore = List.of(
                    Component.text("§7Stocker votre argent et celle de votre ville"),
                    Component.text("§7Contribuer au développement de votre ville"),
                    Component.empty(),
                    Component.text("§cVous devez être Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CITY_BANK) + " pour débloquer ceci")
            );
        }
        return lore;
    }
}
