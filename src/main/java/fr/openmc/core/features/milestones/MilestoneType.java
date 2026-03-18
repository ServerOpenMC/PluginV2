package fr.openmc.core.features.milestones;

import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import lombok.Getter;

@Getter
public enum MilestoneType {
    TUTORIAL(
            new TutorialMilestone(),
		    true
    );

    private final Milestone milestone;
	private final boolean boosBar;

    MilestoneType(Milestone milestone, boolean bossBar) {
        this.milestone = milestone;
		this.boosBar = bossBar;
    }
}
