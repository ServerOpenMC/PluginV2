package fr.openmc.core.features.milestones.listeners;

import fr.openmc.core.features.milestones.Milestone;
import fr.openmc.core.features.milestones.MilestoneModel;
import fr.openmc.core.features.milestones.MilestonesManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Milestone milestone : MilestonesManager.getRegisteredMilestones()) {
            if (!milestone.getPlayerData().containsKey(player.getUniqueId())) {
                milestone.getPlayerData().put(player.getUniqueId(), new MilestoneModel(
                        player.getUniqueId(),
                        milestone.getType(),
                        0,
		                0
                ));
            }
        }
    }
}
