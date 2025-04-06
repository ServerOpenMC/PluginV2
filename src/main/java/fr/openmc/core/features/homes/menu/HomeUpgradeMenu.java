package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.homes.HomesManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeUpgradeMenu extends Menu {

    private final HomeUpgradeManager homeUpgradeManager;
    private final HomesManager homesManager;

    public HomeUpgradeMenu(Player owner) {
        super(owner);
        this.homeUpgradeManager = HomeUpgradeManager.getInstance();
        this.homesManager = HomesManager.getInstance();
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home_upgrade%");
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> items = new HashMap<>();

        int currentHome = homesManager.getHomeLimit(getOwner().getUniqueId());

        int homeMaxLimit = HomeLimits.values().length - 1;

        HomeLimits lastUpgrade = HomeLimits.valueOf("LIMIT_" + homeMaxLimit);
        HomeLimits nextUpgrade = homeUpgradeManager.getNextUpgrade(homeUpgradeManager.getCurrentUpgrade(getOwner().getPlayer())) != null
                ? homeUpgradeManager.getNextUpgrade(homeUpgradeManager.getCurrentUpgrade(getOwner().getPlayer()))
                : lastUpgrade;

        int finalCurrentHome = currentHome;
        items.put(4, new ItemBuilder(this, CustomStack.getInstance("omc_homes:omc_homes_icon_upgrade").getItemStack(), itemMeta -> {
            itemMeta.setDisplayName("§8● §6Améliorer les homes §8(Clique gauche)");
            List<String> lore = new ArrayList<>();
            lore.add("§6Nombre de home actuel: §e" + finalCurrentHome);
            if (nextUpgrade.getLimit() >= lastUpgrade.getLimit()) {
                lore.add("§cVous avez atteint le nombre maximum de homes");
            } else {
                lore.add("§bPrix: §a" + nextUpgrade.getPrice() + " " + EconomyManager.getEconomyIcon());
                lore.add("§bAywenite: §d" + nextUpgrade.getAyweniteCost());
                lore.add("§6Nombre de home au prochain niveau: §e" + nextUpgrade.getLimit());
                lore.add("§7→ Clique gauche pour améliorer");
            }

            itemMeta.setLore(lore);
        }).setOnClick(event -> {
            homeUpgradeManager.upgradeHome(getOwner());
            getOwner().closeInventory();
        }));

        return items;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}
}
