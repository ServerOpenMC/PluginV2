package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class HangingProtection implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Hanging) {
            ProtectionsManager.verify(player, event, entity.getLocation());
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getRemover() instanceof Player player) {
            ProtectionsManager.verify(player, event, event.getEntity().getLocation());
        }
    }
}
