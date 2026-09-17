package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamBlocksManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class DreamBlocksListeners implements Listener {
    private final DreamBlocksManager dreamBlocksManager;

    public DreamBlocksListeners() {
        this.dreamBlocksManager = OMCRegistry.DREAM_FEATURES.DREAM_BLOCKS;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!DreamUtils.isDreamWorld(event.getBlock().getLocation())) return;

        if (dreamBlocksManager.isDreamBlock(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!DreamUtils.isDreamWorld(event.getBlock().getLocation())) return;
        event.blockList().removeIf(block -> dreamBlocksManager.isDreamBlock(block.getLocation()));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!DreamUtils.isDreamWorld(event.getEntity().getLocation())) return;

        event.blockList().removeIf(block -> dreamBlocksManager.isDreamBlock(block.getLocation()));
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.getBlocks().isEmpty()) return;

        if (!DreamUtils.isDreamWorld(event.getBlocks().getFirst().getLocation()))
            return;

        for (Block block : event.getBlocks()) {
            if (dreamBlocksManager.isDreamBlock(block.getLocation())) {
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
            if (dreamBlocksManager.isDreamBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
