package fr.openmc.core.features.milestones;

import org.bukkit.entity.Player;

public class MilestoneUtils {
    public static void completeStep(MilestoneType type, Player player, Enum<? extends MilestoneStep> step) {
        int stepInt = step.ordinal() + 1;

        if (MilestonesManager.getPlayerStep(type, player) >= stepInt) return;

        MilestonesManager.setPlayerStep(type, player, stepInt);
	    MilestonesManager.getMilestoneData(type).get(player.getUniqueId()).setProgress(0);
    }

    public static boolean hasFinishedMilestone(MilestoneType type, Player player) {
        return MilestonesManager.getPlayerStep(type, player) >= type.getMilestone().getSteps().size();
    }
}
