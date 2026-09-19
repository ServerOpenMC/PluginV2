package fr.openmc.core.features.events.contents.halloween.halloween.shop.menu;

import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityChestAction;
import fr.openmc.core.features.city.menu.CityChestMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.ChestPageLimitRewards;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.halloween.shop.WitchShopItem;
import fr.openmc.core.features.events.contents.halloween.halloween.shop.WitchShopManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.level.levelgen.Column;
import org.bukkit.Bukkit;
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
import java.util.stream.Stream;

import static fr.openmc.core.features.city.conditions.CityChestConditions.*;

public class WitchShopStoreMenu extends PaginatedMenu {

    @Getter
    private final int page;

    private final Column.Range line = new Column.Range(10, 16);
    private final int column = 4;

    public WitchShopStoreMenu(Player owner, int page) {
        super(owner);
        this.page = page;

        if (this.page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }

        if (this.page > WitchShopManager.maxPage) {
            throw new IllegalArgumentException("Page must be less than or equal to " + WitchShopManager.maxPage);
        }
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getBottomSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        if (Restart.isRestarting) return null;

        Map<Integer, ItemMenuBuilder> map = new HashMap<>();

        if (hasPreviousPage()) {
            map.put(45, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_BACK_ORANGE, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"));
            }).setPreviousPageButton());
        }

        map.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setOnClick(event -> {
            //TODO open the WitchShopMainMenu
        }));

        if (hasNextPage()) {
            map.put(53, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_NEXT_ORANGE, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"));
            }).setNextPageButton());
        }

        Iterator<WitchShopItem> contents = WitchShopManager.getStoreContent(this.page).iterator();

        for (int c = 0; c < column; c++) {
            for (int l = line.floor(); l <= line.ceiling(); l++) {

                if (!contents.hasNext()) break;

                WitchShopItem item = contents.next();

                map.put(l + (9 * c), new ItemMenuBuilder(this, item.getItem(), itemMeta -> {
                    itemMeta.displayName(item.getName());
                    itemMeta.lore(WitchShopManager.extractLoreForItem(item));
                }).setOnClick(event -> {
                    //TODO open confirm menu if has enough moneyItem
                }));
            }
        }

        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.menus.chest.name",
                Component.text(""), Component.text(this.page)); //TODO
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:"; //TODO
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
        if (Restart.isRestarting) return;
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player player)) return;
        // fixes #1007
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), ()-> MenuLib.updateMenu(player), 5L);
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    public boolean hasNextPage() {
        return this.page < WitchShopManager.maxPage;
    }

    public boolean hasPreviousPage() {
        return this.page > 1;
    }
}
