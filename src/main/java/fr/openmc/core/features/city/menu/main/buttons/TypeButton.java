package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.menu.CityTypeMenu;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class TypeButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
            MenuUtils.runDynamicButtonItem(player, menu, slots, getItemSupplier(menu, city, player))
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            MenuUtils.createButtonItem(
                    contents,
                    slots,
                    getItemSupplier(menu, city, player).get()
            );
        }
    }

    private static Supplier<ItemBuilder> getItemSupplier(Menu menu, City city, Player player) {
        return () -> new ItemBuilder(menu, Material.PAPER, meta -> {
            meta.itemName(TranslationManager.translation("feature.city.menus.main.type.title"));
            meta.lore(getDynamicLore(city, player));
            meta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent -> {
            if (!(city.hasPermission(player.getUniqueId(), CityPermission.CHANGE_TYPE))) return;

            new CityTypeMenu(player).open();
        });
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        boolean hasPermissionChangeType = city.hasPermission(player.getUniqueId(), CityPermission.CHANGE_TYPE);
        boolean showWarCommand = city.getType().equals(CityType.WAR) && city.hasPermission(player.getUniqueId(), CityPermission.LAUNCH_WAR);
        boolean hasCooldown = !DynamicCooldownManager.isReady(city.getUniqueId(), "city:type");

        List<Component> lore = new ArrayList<>();
        lore.add(TranslationManager.translation(
                "feature.city.menus.main.type.lore.status",
                Component.text(PlainTextComponentSerializer.plainText().serialize(city.getType().getDisplayName()).toLowerCase(Locale.ROOT))
        ));

        if (showWarCommand) {
            lore.add(TranslationManager.translation("feature.city.menus.main.type.lore.war_command"));
        }

        if (hasCooldown) {
            lore.add(TranslationManager.translation(
                    "feature.city.menus.main.type.lore.cooldown",
                    Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type")))
                            .decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY)
            ));
        }

        if (hasPermissionChangeType) {
            lore.add(TranslationManager.translation("feature.city.menus.main.type.lore.click"));
        }

        return lore;
    }
}
