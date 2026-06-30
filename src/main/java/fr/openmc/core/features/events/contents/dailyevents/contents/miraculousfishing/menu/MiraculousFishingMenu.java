package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiraculousFishingMenu extends Menu {

    public MiraculousFishingMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return DailyEventsManager.MIRACULOUS_FISHING.getName();
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
                && DailyEventsManager.getActiveDailyEvent() instanceof MiraculousFishingEvent;

        inventory.put(11, new ItemMenuBuilder(this, Material.FISHING_ROD, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.miraculousfishing.menu.info.fishing_speed.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.miraculousfishing.menu.info.fishing_speed.lore",
                    Component.text(MiraculousFishingManager.FISHING_SPEED_MODIFIER * 100).color(NamedTextColor.AQUA)
            ));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }));

        inventory.put(13, new ItemMenuBuilder(this, Material.DROWNED_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.miraculousfishing.menu.info.sea_creature.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.dailyevents.miraculousfishing.menu.info.sea_creature.lore"));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.SEA_CREATURE.openMenu(getOwner())));

        inventory.put(15, new ItemMenuBuilder(this, Material.MAP, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.miraculousfishing.menu.info.loot_table.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.dailyevents.miraculousfishing.menu.info.loot_table.lore"));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.MIRACULOUS_FISHING.openMenu(getOwner())));

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
