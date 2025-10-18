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
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
                    itemMeta.itemName(Component.text("§aLe coffre de la ville"));
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
        boolean hasPermissionChest = city.hasPermission(player.getUniqueId(), CityPermission.CHEST);
        List<Component> lore;
        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CHEST)) {
            lore = List.of(
                    Component.text("§7Acceder au coffre de votre ville pour"),
                    Component.text("§7stocker des items en commun"),
                    Component.empty(),
                    Component.text("§cVous devez être niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CHEST) + " pour débloquer ceci")
            );
        } else {
            if (hasPermissionChest) {
                if (city.getChestWatcher() != null) {
                    lore = List.of(
                            Component.text("§7Acceder au coffre de votre ville pour"),
                            Component.text("§7stocker des items en commun"),
                            Component.empty(),
                            Component.text("§7Ce coffre est déjà ouvert par §c" + Bukkit.getPlayer(city.getChestWatcher()).getName())
                    );
                } else {
                    lore = List.of(
                            Component.text("§7Acceder au coffre de votre ville pour"),
                            Component.text("§7stocker des items en commun"),
                            Component.empty(),
                            Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU COFFRE")
                    );
                }
            } else {
                lore = List.of(
                        Component.text("§7Vous n'avez pas le §cdroit de visionner le coffre !")
                );
            }
        }
        return lore;
    }
}
