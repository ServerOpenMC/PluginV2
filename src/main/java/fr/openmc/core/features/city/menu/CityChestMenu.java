package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityChestAction;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

import static fr.openmc.core.features.city.conditions.CityChestConditions.*;

public class CityChestMenu extends PaginatedMenu {

    private final City city;
    @Getter
    private final int page;

    public CityChestMenu(Player owner, City city, int page) {
        super(owner);
        this.city = city;
        this.page = page;

        if (this.page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }

        if (this.page > this.city.getChestPages()) {
            throw new IllegalArgumentException("Page must be less than or equal to " + this.city.getChestPages());
        }

        city.setChestWatcher(getOwner().getUniqueId());
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getBottomSlots(getInventorySize());
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        ItemStack[] contents = city.getChestContent(this.page);

        if (contents == null) {
            return Collections.emptyList();
        }

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                contents[i] = new ItemStack(Material.AIR);
            }
        }

        return Arrays.asList(contents);
    }

    private static final List<Integer> CITY_ITEM_SLOTS =
            IntStream.rangeClosed(0, 44)
                    .boxed()
                    .toList();
    @Override
    public List<Integer> getTakableSlot() {
        return CITY_ITEM_SLOTS;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        if (Restart.isRestarting) return null;

        Player player = getOwner();

        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§7Fermer"));
        }).setOnClick(inventoryClickEvent -> {
            exit(city, getInventory());
            player.closeInventory();
        }));

        if (hasPreviousPage()) {
            map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
                itemMeta.displayName(Component.text("§cPage précédente"));
            }).setOnClick(inventoryClickEvent -> {
                if (hasPreviousPage()) {
                    Inventory inv = inventoryClickEvent.getInventory();

                    exit(city, inv);

                    new CityChestMenu(player, city, this.getPage() - 1).open();
                    city.setChestWatcher(getOwner().getUniqueId());
                }
            }));
        }
        if (hasNextPage()) {
            map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
                itemMeta.displayName(Component.text("§aPage suivante"));
            }).setOnClick(inventoryClickEvent -> {
                if (hasNextPage()) {
                    Inventory inv = inventoryClickEvent.getInventory();

                    exit(city, inv);

                    new CityChestMenu(player, city, this.getPage() + 1).open();
                    city.setChestWatcher(getOwner().getUniqueId());
                }
            }));
        }

        if (city.hasPermission(getOwner().getUniqueId(), CPermission.CHEST_UPGRADE) && city.getChestPages() < 5) {
            map.put(47, new ItemBuilder(this, Material.ENDER_CHEST, itemMeta -> {
                itemMeta.displayName(Component.text("§aAméliorer le coffre"));
                itemMeta.lore(List.of(
                        Component.text("§7Votre ville doit avoir : "),
                        Component.text("§8- §6" + city.getChestPages() * UPGRADE_PER_MONEY).append(Component.text(EconomyManager.getEconomyIcon())).decoration(TextDecoration.ITALIC, false),
                        Component.text("§8- §d" + city.getChestPages() * UPGRADE_PER_AYWENITE + " d'Aywenite"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR AMELIORER LE COFFRE")
                ));
            }).setOnClick(inventoryClickEvent -> {
                if (!canCityChestUpgrade(city, player)) return;

                CityChestAction.upgradeChest(player, city);
                exit(city, getInventory());
                player.closeInventory();
            }));
        }
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Coffre de " + this.city.getName() + " - Page " + this.page;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player player)) {
            return;
        }

        City city = CityManager.getPlayerCity(player.getUniqueId()); // Permet de charger les villes en background
        if (city == null) {
            return;
        }

        Inventory inv = event.getInventory();
        exit(city, inv);
    }

    private void exit(City city, Inventory inv) {
        for (int i = 45; i < 54; i++) {
            inv.clear(i);
        }

        city.saveChestContent(this.getPage(), inv.getContents());
        city.setChestWatcher(null);
    }

    public boolean hasNextPage() {
        return this.page < this.city.getChestPages();
    }


    public boolean hasPreviousPage() {
        return this.page > 1;
    }
}
