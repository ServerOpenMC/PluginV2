package fr.openmc.core.features.milestones.tutorial.listeners;

import fr.openmc.core.features.bossbar.BossbarManager;
import fr.openmc.core.features.bossbar.BossbarsType;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialBossBar;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TutorialBossBarEvent implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int maxStep = TutorialStep.values().length;
        int step = MilestonesManager.getPlayerStep(MilestoneType.TUTORIAL, player);

        if (step > maxStep) {
            return;
        }

        TutorialBossBar.addTutorialBossBarForPlayer(
                player,
                Component.text(TutorialBossBar.PLACEHOLDER_TUTORIAL_BOSSBAR.formatted(
                        step + 1,
                        TutorialStep.values()[step].getQuest().getName(player.getUniqueId())
                )),
                (float) (step + 1) / maxStep
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossbarManager.removeBossBar(BossbarsType.TUTORIAL, event.getPlayer());
    }
}
