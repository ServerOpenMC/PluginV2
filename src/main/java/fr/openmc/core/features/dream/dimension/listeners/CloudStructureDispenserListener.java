package fr.openmc.core.features.dream.dimension.listeners;

import fr.openmc.core.features.dream.models.registry.DreamStructure;
import fr.openmc.core.features.dream.registries.DreamStructuresRegistry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CloudStructureDispenserListener implements Listener {
    @EventHandler
    public void onDispenserInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();

        if (block.getType() != Material.DISPENSER) return;

        if (!event.getPlayer().getLocation().getChunk().getStructures(DreamStructure.CLOUD_CASTLE.getStructure()).isEmpty())
            event.setCancelled(true);

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.DISPENSER && DreamStructuresRegistry.isInDreamStructure(event.getPlayer(), DreamStructure.CLOUD_CASTLE)) {
            event.setCancelled(true);
        }
    }

}
