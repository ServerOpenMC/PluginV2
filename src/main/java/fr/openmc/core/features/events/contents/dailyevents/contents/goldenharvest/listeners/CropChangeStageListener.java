package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.registry.items.keys.KeyBlock;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CropChangeStageListener implements Listener {

    @EventHandler
    public void onCropHasGrowed(BlockGrowEvent event) {
        BlockType blockType = event.getBlock().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        if (GoldenHarvestManager.getGoldenCropsMapping().get(keyBlock) == null) return;

        BlockData data = event.getNewState().getBlockData();
        if (!(data instanceof Ageable ageable)) return;

        if (ageable.getAge() == ageable.getMaximumAge()) {
            System.out.println("Crop has reached maximum age");
            if (ThreadLocalRandom.current().nextDouble() >= GoldenHarvestManager.OBESE_CROP_CHANCE) return;
            System.out.println("place obese CROPS");
            GoldenHarvestManager.setObeseCrop(event.getBlock());
            // todo sfx
        }
    }
}
