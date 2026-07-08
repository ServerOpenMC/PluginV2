package fr.openmc.core.registry.ambient.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.PlayerSetTimeNMS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.TimeSkipEvent;

public class AmbientFixedTimeListener implements Listener {
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        reapplyTime(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        reapplyTime(player);
    }

    @EventHandler
    public void onTimeChange(TimeSkipEvent event) {
        for (Player player : event.getWorld().getPlayers()) {
            reapplyTime(player);
        }
    }

    /**
     * Réapplique le temps sur un joueur
     * @param player le joueur ciblé
     */
    private void reapplyTime(Player player) {
        if (!CustomAmbient.ACTIVE_AMBIENTS.containsKey(player.getUniqueId())) return;

        CustomAmbient ambientApplied = OMCRegistry.CUSTOM_AMBIENTS.getOrThrow(
                CustomAmbient.ACTIVE_AMBIENTS.get(player.getUniqueId()));
        if (ambientApplied.getAmbientBuilder().getTimeFixed() != null) {
            PlayerSetTimeNMS.sendPacketSetTime(player, ambientApplied.getAmbientBuilder().getTimeFixed());
        }
    }
}
