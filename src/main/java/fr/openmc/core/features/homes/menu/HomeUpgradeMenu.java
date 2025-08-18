package fr.openmc.core.features.homes.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.items.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomeUpgradeMenu extends Menu {

    public HomeUpgradeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Homes - Amélioration";
    }

    @Override
    public String getTexture() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home_upgrade%");
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> items = new HashMap<>();

        int currentHome = HomesManager.getHomeLimit(getOwner().getUniqueId());

        int homeMaxLimit = HomeLimits.values().length - 1;

            HomeLimits lastUpgrade = HomeLimits.valueOf("LIMIT_" + homeMaxLimit);
            HomeLimits nextUpgrade = HomeUpgradeManager.getNextUpgrade(HomeUpgradeManager.getCurrentUpgrade(getOwner())) != null
                    ? HomeUpgradeManager.getNextUpgrade(HomeUpgradeManager.getCurrentUpgrade(getOwner()))
                    : lastUpgrade;

            items.put(4, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_upgrade")).getBest(), itemMeta -> {
                itemMeta.displayName(Component.text("§8● §6Améliorer les homes §8(Clique gauche)"));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("§6Nombre de home actuel: §e" + currentHome));
                if (nextUpgrade.getLimit() >= lastUpgrade.getLimit()) {
                    lore.add(Component.text("§cVous avez atteint le nombre maximum de homes"));
                } else {
                    lore.add(Component.text("§bPrix: §a" + nextUpgrade.getPrice() + " " + EconomyManager.getEconomyIcon()));
                    lore.add(Component.text("§bAywenite: §d" + nextUpgrade.getAyweniteCost()));
                    lore.add(Component.text("§6Nombre de home au prochain niveau: §e" + nextUpgrade.getLimit()));
                    lore.add(Component.text("§7→ Clique gauche pour améliorer"));
                }

                itemMeta.lore(lore);
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
