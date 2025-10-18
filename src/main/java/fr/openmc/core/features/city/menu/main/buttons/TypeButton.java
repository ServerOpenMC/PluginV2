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
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
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
            meta.itemName(Component.text("§5Le statut de votre ville"));
            meta.lore(getDynamicLore(city, player));
            meta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent -> {
            if (!(city.hasPermission(player.getUniqueId(), CityPermission.TYPE))) return;

            new CityTypeMenu(player).open();
        });
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        boolean hasPermissionChangeType = city.hasPermission(player.getUniqueId(), CityPermission.TYPE);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Votre ville est en §5" + city.getType().getDisplayName().toLowerCase(Locale.ROOT)));

        if (city.getType().equals(CityType.WAR) && city.hasPermission(player.getUniqueId(), CityPermission.LAUNCH_WAR)) {
            lore.add(Component.empty());
            lore.add(Component.text("§7Vous pouvez lancer une guerre avec §c/war"));
        }

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
            lore.add(Component.empty());
            lore.add(Component.text("§cCooldown §7: " +
                    DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type"))));
        }

        if (hasPermissionChangeType) {
            lore.add(Component.empty());
            lore.add(Component.text("§e§lCLIQUEZ ICI POUR LE CHANGER"));
        }

        return lore;
    }
}
