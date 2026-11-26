package fr.openmc.core.features.events.contents.christmas.mecanism.dailyevent.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.christmas.mecanism.dailyevent.BlizzardEvent;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
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

public class BlizzardEventMenu extends Menu {

    public BlizzardEventMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return OMCRegistry.DAILY_EVENTS.BLIZZARD.getName();
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

        boolean isActived = DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof BlizzardEvent;

        inventory.put(11, new ItemMenuBuilder(this, Material.ICE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.blizzard.menu.info.gifts_type.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.gifts_type.lore"));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ -> new GiftTypeMenu(getOwner()).open()));

        inventory.put(4, new ItemMenuBuilder(this, Material.SEA_LANTERN, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.blizzard.menu.info.rift.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.rift.lore"
            ));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }));

        inventory.put(13, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.blizzard.menu.info.gingerbread.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.blizzard.menu.info.gingerbread.lore"
            ));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }));

        inventory.put(15, new ItemMenuBuilder(this, Material.CHEST, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.blizzard.menu.info.chests.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.dailyevents.blizzard.menu.info.chests.lore"));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ -> new ChestTypeMenu(getOwner()).open()));

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
