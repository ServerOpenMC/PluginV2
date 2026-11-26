package fr.openmc.core.features.events.contents.christmas.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasWorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;

public class DimensionListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onFireBurnBlock(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (!ChristmasWorldUtils.isChristmasWorld(block.getWorld())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIceBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!ChristmasWorldUtils.isChristmasWorld(block.getWorld())) return;
        if (block.getType() != Material.ICE) return;

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (block.getType() == Material.WATER) {
                block.setType(Material.AIR);
            }
        });
    }
}
