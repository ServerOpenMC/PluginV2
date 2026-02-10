package fr.openmc.core.features.milestones;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.milestones.tutorial.TutorialBossBar;
import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MilestoneUtils {
    public static void completeStep(MilestoneType type, Player player, Enum step) {
        int stepInt = step.ordinal() + 1;

        if (MilestonesManager.getPlayerStep(type, player) >= stepInt) return;

        MilestonesManager.setPlayerStep(type, player, stepInt);
	    
	    MilestonesManager.getMilestoneData(type).get(player.getUniqueId()).setProgress(0);

		if (!type.isBoosBar()) return;
		
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

        if (step >= maxStep) return;
		String progressStr = "";
		if (step == 0) {
			progressStr = " (" + TutorialMilestone.playerData.get(player.getUniqueId()).getProgress() + " / 30)";
		}

        TutorialBossBar.addTutorialBossBarForPlayer(
                player,
                Component.text(TutorialBossBar.PLACEHOLDER_TUTORIAL_BOSSBAR.formatted(
                        step + 1,
                        TutorialStep.values()[step].getQuest().getName(player.getUniqueId()) + progressStr
                )),
		        step == 0 ? (float) TutorialStep.BREAK_AYWENITE.getQuest().getProgress(player.getUniqueId()) / 30 : (float) (step + 1) / maxStep
        );
    }
}
