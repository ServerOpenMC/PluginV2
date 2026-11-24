package fr.openmc.core.features.events.contents.christmas.mecanism.dailyevent.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
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

public class GiftTypeMenu extends Menu {

    public GiftTypeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.dailyevents.blizzard.menu.info.gifts.name");
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

        inventory.put(11, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.COMMON_GIFT, itemMeta -> {
            itemMeta.displayName(GiftType.COMMON.getGiftTable().getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.gifts.common.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.COMMON_GIFT.openMenu(getOwner())));

        inventory.put(12, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.RARE_GIFT, itemMeta -> {
            itemMeta.displayName(GiftType.RARE.getGiftTable().getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.gifts.rare.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.RARE_GIFT.openMenu(getOwner())));

        inventory.put(14, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.LEGENDARY_GIFT, itemMeta -> {
            itemMeta.displayName(GiftType.LEGENDARY.getGiftTable().getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.gifts.legendary.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.LEGENDARY_GIFT.openMenu(getOwner())));

        inventory.put(15, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ONIRISIME_GIFT, itemMeta -> {
            itemMeta.displayName(GiftType.ONIRISME.getGiftTable().getName());
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.gifts.onirism.lore"
            ));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.ONIRISM_GIFT.openMenu(getOwner())));

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
