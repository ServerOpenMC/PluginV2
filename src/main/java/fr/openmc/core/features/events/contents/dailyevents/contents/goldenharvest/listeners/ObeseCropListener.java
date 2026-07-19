package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners;

import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops.ObeseCropsRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ObeseCropListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onObeseCropBreak(BlockBreakEvent event) {
        if (!ObeseCropsRegistry.isObeseCrop(event.getBlock().getLocation())) return;

        ObeseCropsRegistry.unmark(event.getBlock().getLocation());
    }
}
