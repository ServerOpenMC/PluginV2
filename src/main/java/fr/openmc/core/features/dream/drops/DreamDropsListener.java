package fr.openmc.core.features.dream.drops;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DreamDropsListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!block.getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        Material type = block.getType();

        ItemStack customDrop = DreamDropsManager.getCustomDrop(type);

        event.setDropItems(false);
        event.setExpToDrop(0);

        if (customDrop != null && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            block.getWorld().dropItemNaturally(block.getLocation(), customDrop);
        }
    }
}