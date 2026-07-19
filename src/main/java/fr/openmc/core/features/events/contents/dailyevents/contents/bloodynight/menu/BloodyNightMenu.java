package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
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

public class BloodyNightMenu extends Menu {

    public BloodyNightMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return DailyEventsManager.BLOODY_NIGHT.getName();
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

        inventory.put(11, new ItemMenuBuilder(this, Material.STRIDER_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.menu.info.bloody_monster.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.bloody_night.menu.info.bloody_monster.lore"));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ -> new BloodyMonsterMenu(getOwner()).open()));

    inventory.put(13, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.VAMPIRE_HEAD, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.menu.info.vampire_boss.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.bloody_night.menu.info.vampire_boss.lore",
                    Component.text(BloodyNightManager.VAMPIRE_SPAWN_TIME / 60 / 20, NamedTextColor.RED)));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.VAMPIRE.openMenu(getOwner())));

        inventory.put(15, new ItemMenuBuilder(this, Material.NETHERITE_SPEAR, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.menu.info.bloody_raid.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.dailyevents.bloody_night.menu.info.bloody_raid.lore",
                    Component.text(BloodyNightManager.RAID_INTERVAL / 60 / 20, NamedTextColor.RED)));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }));

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
