package fr.openmc.core.features.events.contents.christmas.contents.dailyevent.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestTypeMenu extends Menu {

    public ChestTypeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.dailyevents.blizzard.menu.info.chest.name");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        inventory.put(11, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_WHITE_GIFT_BOX_BLOCK, itemMeta -> {
            itemMeta.displayName(OMCRegistry.CUSTOM_LOOT_TABLES.SMALL_CHRISTMAS_CHEST.getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.small_chest.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.SMALL_CHRISTMAS_CHEST.openMenu(getOwner())));

        inventory.put(13, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_BLUE_GIFT_BOX_BLOCK, itemMeta -> {
            itemMeta.displayName(OMCRegistry.CUSTOM_LOOT_TABLES.MEDIUM_CHRISTMAS_CHEST.getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.medium_chest.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.MEDIUM_CHRISTMAS_CHEST.openMenu(getOwner())));

        inventory.put(15, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_YELLOW_GIFT_BOX_BLOCK, itemMeta -> {
            itemMeta.displayName(OMCRegistry.CUSTOM_LOOT_TABLES.LARGE_CHRISTMAS_CHEST.getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.large_chest.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.LARGE_CHRISTMAS_CHEST.openMenu(getOwner())));

        inventory.put(18, new ItemMenuBuilder(this, Material.ARROW, true));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
