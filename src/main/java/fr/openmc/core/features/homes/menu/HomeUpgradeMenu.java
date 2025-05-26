package fr.openmc.core.features.homes.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeUpgradeMenu extends Menu {

    public HomeUpgradeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home_upgrade%");
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> items = new HashMap<>();

        try {
            int currentHome = HomesManager.getHomeLimit(getOwner().getUniqueId());

            int homeMaxLimit = HomeLimits.values().length - 1;

            HomeLimits lastUpgrade = HomeLimits.valueOf("LIMIT_" + homeMaxLimit);
            HomeLimits nextUpgrade = HomeUpgradeManager.getNextUpgrade(HomeUpgradeManager.getCurrentUpgrade(getOwner().getPlayer())) != null
                    ? HomeUpgradeManager.getNextUpgrade(HomeUpgradeManager.getCurrentUpgrade(getOwner().getPlayer()))
                    : lastUpgrade;

            int finalCurrentHome = currentHome;
            items.put(4, new ItemBuilder(this, CustomItemRegistry.getByName("omc_homes:omc_homes_icon_upgrade").getBest(), itemMeta -> {
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
                HomeUpgradeManager.upgradeHome(getOwner());
                getOwner().closeInventory();
            }));

            return items;
        } catch (Exception e) {
            MessagesManager.sendMessage(getOwner(), Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            getOwner().closeInventory();
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
