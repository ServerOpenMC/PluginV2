package fr.openmc.core.features.milestones;

import fr.openmc.core.features.dream.milestone.DreamMilestone;
import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import lombok.Getter;

@Getter
public enum MilestoneType {
    TUTORIAL(
            new TutorialMilestone()
    ),
	DREAM(
			new DreamMilestone()
	);

    private final Milestone<?> milestone;

    MilestoneType(Milestone<?> milestone) {
        this.milestone = milestone;
    }
}
