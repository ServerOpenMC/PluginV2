package fr.openmc.core.features.dream.mecanism.blocksdrops;

import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ChangeBlockDropsListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!DreamUtils.isDreamWorld(block.getLocation())) return;

        Material type = block.getType();

        ItemStack customDrop = DreamBlocksDropsManager.getCustomDrop(type);

        event.setDropItems(false);
        event.setExpToDrop(0);

        if (customDrop != null && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            block.getWorld().dropItemNaturally(block.getLocation(), customDrop);
        }
    }
}
