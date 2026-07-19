package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightManager;
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

public class BloodyMonsterMenu extends Menu {

    public BloodyMonsterMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.menu.info.bloody_monster.name");
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

        inventory.put(11, new ItemMenuBuilder(this, Material.CRIMSON_NYLIUM, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.corrupted_mob"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.bloody_night.menu.info.bloody_monster.lore",
                    Component.text(BloodyNightManager.CORRUPTED_CHANCE * 100 + "%", NamedTextColor.DARK_RED)));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.CORRUPTED_MOB.openMenu(getOwner())));

        inventory.put(12, new ItemMenuBuilder(this, Material.RED_DYE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.cursed_mob"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.bloody_night.menu.info.bloody_monster.lore",
                    Component.text(BloodyNightManager.CURSED_CHANCE * 100 + "%", NamedTextColor.RED)));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.CURSED_MOB.openMenu(getOwner())));

        inventory.put(14, new ItemMenuBuilder(this, Material.PURPLE_DYE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.enraged_mob"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.bloody_night.menu.info.bloody_monster.lore",
                    Component.text(BloodyNightManager.ENRAGED_CHANCE * 100 + "%", NamedTextColor.DARK_PURPLE)));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.ENRAGED_MOB.openMenu(getOwner())));

        inventory.put(15, new ItemMenuBuilder(this, Material.YELLOW_DYE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.ancient_mob"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.bloody_night.menu.info.bloody_monster.lore",
                    Component.text(BloodyNightManager.ANCIENT_CHANCE * 100 + "%", NamedTextColor.YELLOW)));
        }).setOnClick(_ ->
                OMCRegistry.CUSTOM_LOOT_TABLES.ANCIENT_MOB.openMenu(getOwner())));

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
