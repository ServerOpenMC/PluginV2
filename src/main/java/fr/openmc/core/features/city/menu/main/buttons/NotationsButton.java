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
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
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
                    itemMeta.itemName(Component.text("§3La Notation de votre ville"));
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
            lore = List.of(
                    Component.text("§7Notation de la ville : §9" + Math.floor(notation.getTotalNote()) + "§7/§9" + NotationNote.getMaxTotalNote()),
                    Component.text("§7Argent remporté : §6" + EconomyManager.getFormattedSimplifiedNumber(notation.getMoney()) + EconomyManager.getEconomyIcon()),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR VOIR LA NOTATION")
            );
        } else {
            if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.NOTATION)) {
                lore = List.of(
                        Component.text("§cVous n'avez pas de notation")
                );
            } else {
                lore = List.of(
                        Component.text("§cVous devez être niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.NOTATION) + " pour débloquer ceci")
                );
            }
        }
        return lore;
    }
}
