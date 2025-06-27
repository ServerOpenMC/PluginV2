package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportProtection implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (!ProtectionsManager.canInteract(player, event.getTo())) {
            event.setCancelled(true);

            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            }
        }
    }
}
