package fr.openmc.core.features.events.contents.halloween.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.witchhouse.TerrorHouseDimensionManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

// dupli
public class TramplingProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            if (!event.getEntity().getWorld().equals(TerrorHouseDimensionManager.HALLOWEEN_WORLD))
                return;

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTrampling(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() == Material.FARMLAND) {
                if (!event.getPlayer().getWorld().equals(TerrorHouseDimensionManager.HALLOWEEN_WORLD))
                    return;

                if (!event.getPlayer().isOp())
                    event.setCancelled(true);
            }
        }
    }
}
