package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoldenHarvestMenu extends Menu {

    public GoldenHarvestMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return DailyEventsManager.GOLDEN_HARVEST.getName();
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
                && DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent;

        inventory.put(11, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.GOLDEN_BEETROOT, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.golden_harvest.menu.info.golden_crops.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.golden_harvest.menu.info.golden_crops.lore",
                    Component.text(GoldenHarvestManager.GOLDEN_CROP_ON_CROP_CHANCE * 100 + "%", NamedTextColor.AQUA),
                    Component.text(GoldenHarvestManager.GOLDEN_CROP_ON_OBESE_CHANCE * 100 + "%", NamedTextColor.AQUA)));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ -> new GoldenCropsInfoMenu(getOwner()).open()));

    inventory.put(13, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.FERMENTUM, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.golden_harvest.menu.info.fermentum.name"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.dailyevents.golden_harvest.menu.info.fermentum.lore"));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ ->
                Bukkit.dispatchCommand(getOwner().getPlayer(), "itemsadder:iaguide omc_daily_events:fermentum")));

        inventory.put(15, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.OBESE_GOLDEN_APPLE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.dailyevents.golden_harvest.menu.info.obese_crops.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.dailyevents.golden_harvest.menu.info.obese_crops.lore",
                    Component.text(GoldenHarvestManager.OBESE_CROP_CHANCE * 100 + "%", NamedTextColor.AQUA)));
            itemMeta.setEnchantmentGlintOverride(isActived);
        }).setOnClick(_ -> new ObeseCropsInfoMenu(getOwner()).open()));

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
