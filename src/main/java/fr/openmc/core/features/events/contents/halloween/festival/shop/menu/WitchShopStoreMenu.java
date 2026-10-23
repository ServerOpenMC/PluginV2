package fr.openmc.core.features.events.contents.halloween.festival.shop.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopItem;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.levelgen.Column;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WitchShopStoreMenu extends PaginatedMenu {

    @Getter
    private final int page;

    private final Column.Range LINE = new Column.Range(1, 7);
    private final int COLUMN = 4;

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
        return StaticSlots.combine(StaticSlots.getBottomSlots(getInventorySize()),
                StaticSlots.combine(StaticSlots.getLeftSlots(getInventorySize()),
                        StaticSlots.combine(StaticSlots.getRightSlots(getInventorySize()),
                                List.of(36, 37, 38, 39, 40, 41, 42, 43, 44)) // LINE 5 in the LARGEST Inventory
                )
        );
    }

    @Override
    public List<ItemStack> getItems() {

        List<ItemStack> items = new ArrayList<>();

        Iterator<WitchShopItem> contents = WitchShopManager.getStoreContent(this.page).iterator();

        for (int c = 0; c < COLUMN; c++) {
            for (int l = LINE.floor(); l <= LINE.ceiling(); l++) {

                if (!contents.hasNext()) break;

                WitchShopItem item = contents.next();

                items.add(new ItemMenuBuilder(this, item.getItem(), itemMeta -> {
                    itemMeta.displayName(item.getName());
                    itemMeta.lore(WitchShopManager.extractLoreForItem(item));
                }).setOnClick(event -> {
                    new ConfirmBuyMenu(getOwner(), item);
                }));
            }
        }

        return items;
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
            new WitchShopMainMenu(getOwner()).open();
        }));

        if (hasNextPage()) {
            map.put(53, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_NEXT_ORANGE, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"));
            }).setNextPageButton());
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
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_store_menu:");
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
