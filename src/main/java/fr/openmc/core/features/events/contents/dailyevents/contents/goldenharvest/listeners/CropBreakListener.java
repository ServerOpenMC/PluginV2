package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops.ObeseCropsRegistry;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import org.bukkit.block.BlockType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CropBreakListener implements Listener {
    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;
        if (ThreadLocalRandom.current().nextDouble() > GoldenHarvestManager.GOLDEN_CROP_ON_CROP_CHANCE) return;

        BlockType blockType = event.getBlock().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);

        ItemLoot loot = GoldenHarvestManager.getGoldenCropsOnBreakMapping().get(keyBlock);
        if (loot == null) return;

        System.out.println("give rewards");
        loot.run(event.getPlayer(), event.getBlock().getLocation());
        // todo sfx si loots pas empty + message chance
    }

    @EventHandler
    public void onObeseCropBreak(CustomBlockBreakEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent)) return;
        if (ThreadLocalRandom.current().nextDouble() > GoldenHarvestManager.GOLDEN_CROP_ON_OBESE_CHANCE) return;
        if (!ObeseCropsRegistry.isObeseCrop(event.getBlock().getLocation())) return;

        CustomBlock customBlock = CustomBlock.byItemStack(event.getCustomBlockItem());
        KeyBlock keyBlock = KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.getOrThrow(customBlock.getItemStack()));

        ItemLoot loot = GoldenHarvestManager.getGoldenCropsOnBreakMapping().get(keyBlock);
        if (loot == null) return;

        System.out.println("2 give rewards");
        loot.run(event.getPlayer(), event.getBlock().getLocation());
        // todo sfx si loots pas empty
    }
}
