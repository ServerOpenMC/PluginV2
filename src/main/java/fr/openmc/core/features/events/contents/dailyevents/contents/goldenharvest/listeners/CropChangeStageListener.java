package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import dev.lone.itemsadder.api.CustomBlock;
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
    public void onCropHasGrowedForObese(BlockGrowEvent event) {
        BlockType blockType = event.getBlock().getType().asBlockType();
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        if (GoldenHarvestManager.getObeseCropsMapping().get(keyBlock) == null) return;

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

    @EventHandler
    public void onCropHasGrowedForGolden(BlockGrowEvent event) {
        BlockType blockType = event.getBlock().getType().asBlockType();
        System.out.println("blockType" + blockType);
        KeyBlock keyBlock = KeyBlock.vanilla(blockType);
        KeyBlock keyBlockGolden = GoldenHarvestManager.getGoldenCropsOnGrowMapping().get(keyBlock);
        if (keyBlockGolden == null) return;
        System.out.println("2");
        if (ThreadLocalRandom.current().nextDouble() >= GoldenHarvestManager.GOLDEN_CROP_ON_CROP_CHANCE) return;
        System.out.println("cahcen");

        CustomBlock customBlock =  keyBlockGolden.getCustomBlock();

        if (customBlock == null) return;
        System.out.println("place");
        customBlock.place(event.getBlock().getLocation());
        // todo sfx
    }
}
