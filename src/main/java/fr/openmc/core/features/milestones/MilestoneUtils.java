package fr.openmc.core.features.milestones;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.milestones.models.MilestoneType;
import org.bukkit.entity.Player;

public class MilestoneUtils {
    private final static MilestonesManager milestonesManager = OMCRegistry.FEATURES.MILESTONES.get();
    public static void completeStep(MilestoneType type, Player player, Enum<? extends MilestoneStep> step) {
        int stepInt = step.ordinal() + 1;

        if (milestonesManager.getPlayerStep(type, player) >= stepInt) return;

        milestonesManager.setPlayerStep(type, player, stepInt);
	    milestonesManager.getMilestoneData(type).get(player.getUniqueId()).setProgress(0);
    }

    public static boolean hasFinishedMilestone(MilestoneType type, Player player) {
        return milestonesManager.getPlayerStep(type, player) >= type.getMilestone().getSteps().size();
    }
}
