package fr.openmc.core.features.dream.blocks;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class DreamBlocksListeners implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        if (DreamBlocksManager.isDreamBlock(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!event.getBlock().getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;
        event.blockList().removeIf(block -> DreamBlocksManager.isDreamBlock(block.getLocation()));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!event.getEntity().getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        event.blockList().removeIf(block -> DreamBlocksManager.isDreamBlock(block.getLocation()));
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!event.getBlocks().getFirst().getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME))
            return;

        for (Block block : event.getBlocks()) {
            if (DreamBlocksManager.isDreamBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.getBlocks().getFirst().getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME))
            return;

        for (Block block : event.getBlocks()) {
            if (DreamBlocksManager.isDreamBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
