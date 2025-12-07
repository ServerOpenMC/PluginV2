package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamBlocksRegistry;
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
        if (!DreamUtils.isDreamWorld(event.getBlock().getLocation())) return;

        if (DreamBlocksRegistry.isDreamBlock(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!DreamUtils.isDreamWorld(event.getBlock().getLocation())) return;
        event.blockList().removeIf(block -> DreamBlocksRegistry.isDreamBlock(block.getLocation()));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!DreamUtils.isDreamWorld(event.getEntity().getLocation())) return;

        event.blockList().removeIf(block -> DreamBlocksRegistry.isDreamBlock(block.getLocation()));
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.getBlocks().isEmpty()) return;

        if (!DreamUtils.isDreamWorld(event.getBlocks().getFirst().getLocation()))
            return;

        for (Block block : event.getBlocks()) {
            if (DreamBlocksRegistry.isDreamBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlocks().isEmpty()) return;

        if (!DreamUtils.isDreamWorld(event.getBlocks().getFirst().getLocation()))
            return;

        for (Block block : event.getBlocks()) {
            if (DreamBlocksRegistry.isDreamBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
