package fr.openmc.core.features.milestones;

import fr.openmc.core.features.dream.milestone.DreamMilestone;
import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import lombok.Getter;

@Getter
public enum MilestoneType {
    TUTORIAL(
			new TutorialMilestone(),
		    true
    ),
	DREAM(
			new DreamMilestone(),
			false
	);

    private final Milestone milestone;
	private final boolean bossBar;

    MilestoneType(Milestone milestone, boolean bossBar) {
        this.milestone = milestone;
	    this.bossBar = bossBar;
    }
}
