package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeUpgradeMenu extends Menu {

    public HomeUpgradeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.homes.upgrade.menu.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::omc_homes_menus_home_upgrade:");
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> items = new HashMap<>();

        int currentHome = HomesManager.getHomeLimit(getOwner().getUniqueId()).getLimit();

        HomeLimits nextUpgrade = HomeUpgradeManager.getNextUpgrade(HomeUpgradeManager.getCurrentUpgrade(getOwner()));

        items.put(4, new ItemMenuBuilder(this, Objects.requireNonNull(OMCRegistry.CUSTOM_ITEMS.get("omc_homes:omc_homes_icon_upgrade")).getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.homes.upgrade.item.name"));
            if (nextUpgrade == null) {
                itemMeta.lore(TranslationManager.translationLore(
                        "feature.homes.upgrade.lore.max",
                        Component.text(currentHome).color(NamedTextColor.YELLOW)
                ));
            } else {
                itemMeta.lore(TranslationManager.translationLore(
                        "feature.homes.upgrade.lore.available",
                        Component.text(currentHome).color(NamedTextColor.YELLOW),
                        Component.text(nextUpgrade.getPrice()).color(NamedTextColor.GREEN),
                        Component.text(EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false),
                        Component.text(nextUpgrade.getAyweniteCost()).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(nextUpgrade.getLimit()).color(NamedTextColor.YELLOW)
                ));
            }
        }).setOnClick(event -> {
            HomeUpgradeManager.upgradeHome(getOwner());
            getOwner().closeInventory();
        }));

        return items;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
