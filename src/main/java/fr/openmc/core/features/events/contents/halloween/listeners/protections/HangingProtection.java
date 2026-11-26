package fr.openmc.core.features.events.contents.halloween.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.witchhouse.TerrorHouseDimensionManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

// dupli
public class HangingProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Hanging) {
            if (!event.getPlayer().getWorld().equals(TerrorHouseDimensionManager.HALLOWEEN_WORLD))
                return;

            if (!event.getPlayer().isOp())
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (remover != null) {
            if (!remover.getLocation().getWorld().equals(TerrorHouseDimensionManager.HALLOWEEN_WORLD))
                return;

            event.setCancelled(true);
        }
    }
}
