package fr.openmc.core.features.milestones.tutorial.utils;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialBossBar;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class TutorialUtils {
    public static void completeStep(MilestoneType type, Player player, TutorialStep step) {
        int stepInt = step.ordinal() + 1;

        MilestonesManager.setPlayerStep(type, player, stepInt);

        int maxStep = TutorialStep.values().length;

        if (stepInt >= maxStep) {
            TutorialBossBar.hide(player);
            BossbarManager.removeBossBar(BossbarsType.TUTORIAL, player);
            return;
        }

        TutorialBossBar.update(
                player,
                Component.text(TutorialBossBar.PLACEHOLDER_TUTORIAL_BOSSBAR.formatted(
                        (stepInt + 1),
                        TutorialStep.values()[stepInt].getQuest().getName(player.getUniqueId())
                )),
                (float) (stepInt + 1) / maxStep
        );
    }

    public static void setBossBar(Player player) {
        int maxStep = TutorialStep.values().length;
        int step = MilestonesManager.getPlayerStep(MilestoneType.TUTORIAL, player);

        if (step >= maxStep) {
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
}
