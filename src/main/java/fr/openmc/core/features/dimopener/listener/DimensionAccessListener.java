package fr.openmc.core.features.dimopener.listener;

import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.dimopener.DimensionProgress;
import fr.openmc.core.features.dimopener.DimensionState;
import fr.openmc.core.features.dimopener.data.DimensionData;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DimensionAccessListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        checkAccess(event.getPlayer(), to.getWorld().getName(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        checkAccess(event.getPlayer(), to.getWorld().getName(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        checkAccess(player, to.getWorld().getName(), event);
    }

    public static boolean checkAccess(Player player, String worldName, Cancellable event) {
        DimensionData dim = DimensionOpenerManager.getDimensionByWorldName(worldName);
        if (dim == null) return true;

        DimensionProgress progress = DimensionOpenerManager.getProgress(dim.getId());
        DimensionState state = progress != null ? progress.getState() : null;

        if (state != DimensionState.OPENED) {
            event.setCancelled(true);
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation("feature.dimopener.access.denied"),
                    Prefix.DIMOPENER,
                    MessageType.ERROR,
                    true
            );
            return false;
        }
        return true;
    }

}
