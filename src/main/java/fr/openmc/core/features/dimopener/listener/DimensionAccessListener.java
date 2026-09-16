package fr.openmc.core.features.dimopener.listener;

import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DimensionAccessListener implements Listener {

    private final DimensionOpenerManager manager;

    public DimensionAccessListener(DimensionOpenerManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        manager.checkAccess(event.getPlayer(), to.getWorld().getName(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        manager.checkAccess(event.getPlayer(), to.getWorld().getName(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        manager.checkAccess(player, to.getWorld().getName(), event);
    }
}
