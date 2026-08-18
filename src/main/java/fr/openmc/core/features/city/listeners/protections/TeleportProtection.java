package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class TeleportProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public TeleportProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

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

        if (!protectionsManager.canInteract(player, event.getTo())) {
            protectionsManager.verify(player, event, event.getTo());

            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && !player.getGameMode().equals(GameMode.CREATIVE)) {
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            }
        }
    }
}
