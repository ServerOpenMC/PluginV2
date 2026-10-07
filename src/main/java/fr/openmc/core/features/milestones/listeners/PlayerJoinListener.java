package fr.openmc.core.features.milestones.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final MilestonesManager milestonesManager = OMCRegistry.FEATURES.MILESTONES.get();

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Milestone milestone : milestonesManager.getRegisteredMilestones()) {
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
