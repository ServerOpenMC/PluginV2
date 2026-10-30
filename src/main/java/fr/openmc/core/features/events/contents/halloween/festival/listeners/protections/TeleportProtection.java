package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;

// dupli
public class TeleportProtection implements Listener {
    private final Set<PlayerTeleportEvent.TeleportCause> illegalCauses = Set.of(
            PlayerTeleportEvent.TeleportCause.COMMAND,
            PlayerTeleportEvent.TeleportCause.NETHER_PORTAL,
            PlayerTeleportEvent.TeleportCause.PLUGIN,
            PlayerTeleportEvent.TeleportCause.SPECTATE,
            PlayerTeleportEvent.TeleportCause.END_GATEWAY,
            PlayerTeleportEvent.TeleportCause.EXIT_BED
    );
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if (illegalCauses.contains(cause)) return;

        Player player = event.getPlayer();

        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
}
