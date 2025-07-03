package fr.openmc.core.features.milestones.tutorial.listeners;

import fr.openmc.core.features.bossbar.BossbarManager;
import fr.openmc.core.features.bossbar.BossbarsType;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TutorialBossBarEvent implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        TutorialUtils.setBossBar(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossbarManager.removeBossBar(BossbarsType.TUTORIAL, event.getPlayer());
    }
}
