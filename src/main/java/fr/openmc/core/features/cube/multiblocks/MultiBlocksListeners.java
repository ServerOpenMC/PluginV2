package fr.openmc.core.features.cube.multiblocks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class MultiBlocksListeners implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            mb.onBlockBreak(event);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            mb.onExplosion(event);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            mb.onExplosion(event);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            mb.onPiston(event);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            mb.onPiston(event);
        }
    }
}