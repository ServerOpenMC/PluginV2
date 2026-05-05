package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mascots.menu.MascotMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotsDeadMenu;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MascotsButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int[] slots) {
        Player player = menu.getOwner();
        Mascot mascot = city.getMascot();

        LivingEntity mob = null;
        if (mascot != null) {
            mob = (LivingEntity) mascot.getEntity();
        }

        if (mascot != null && !mascot.isAlive()) {
            MenuUtils.runDynamicButtonItem(player, menu, slots, getItemSupplier(menu, city, mascot, mob, player))
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            MenuUtils.createButtonItem(
                    contents,
                    slots,
                    getItemSupplier(menu, city, mascot, mob, player).get()
            );
        }
    }

    private static Supplier<ItemBuilder> getItemSupplier(Menu menu, City city, Mascot mascot, LivingEntity mob, Player player) {
        return () -> new ItemBuilder(menu, Material.PAPER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.menus.main.mascots.title"));
            itemMeta.lore(getDynamicLore(city, mascot, mob));
            itemMeta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent -> {
            if (mascot == null) return;
            if (mob == null) return;

            if (!mascot.isAlive()) {
                new MascotsDeadMenu(player, city.getUniqueId()).open();
                return;
            }

            new MascotMenu(player, mascot).open();
        });
    }

    private static List<Component> getDynamicLore(City city, Mascot mascot, LivingEntity mob) {
        List<Component> lore;
        double maxHealth;
        if (mascot != null) {
            if (mob != null) {
                AttributeInstance maxHealthAttribute = mob.getAttribute(Attribute.MAX_HEALTH);
                if (maxHealthAttribute == null) {
                    maxHealth = 20.0;
                } else {
                    maxHealth = maxHealthAttribute.getValue();
                }

                if (!mascot.isAlive()) {
                    lore = TranslationManager.translationLore(
                            "feature.city.menus.main.mascots.lore.dead",
                            Component.text(Math.floor(mob.getHealth())).color(NamedTextColor.RED),
                            Component.text(maxHealth).color(NamedTextColor.RED),
                            Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:immunity"))),
                            Component.text(mascot.getLevel()).color(NamedTextColor.RED)
                    );
                } else {
                    lore = TranslationManager.translationLore(
                            "feature.city.menus.main.mascots.lore.alive",
                            Component.text(Math.floor(mob.getHealth())).color(NamedTextColor.RED),
                            Component.text(maxHealth).color(NamedTextColor.RED),
                            Component.text(mascot.getLevel()).color(NamedTextColor.RED)
                    );
                }
            } else {
                lore = TranslationManager.translationLore("feature.city.menus.main.mascots.lore.not_found");
            }
        } else {
            lore = TranslationManager.translationLore("feature.city.menus.main.mascots.lore.missing");
        }

        return lore;
    }
}
