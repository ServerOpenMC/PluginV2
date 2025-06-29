package fr.openmc.core.features.milestones;

import fr.openmc.core.CommandsManager;

import java.util.HashSet;
import java.util.Set;

public class MilestonesManager {
	
	private final Set<Milestone> milestones;
	
	public MilestonesManager() {
		this.milestones = new HashSet<>();
	}

	/**
	 * Enregistre tous les millestones
	 *
	 * @return Instance of MilestonesManager
	 */
	public MilestonesManager registerMilestones(Milestone[] milestones) {
		for (Milestone milestone : milestones) {
			if (milestone != null) {
				this.milestones.add(milestone);
			}
		}
		return this;
	}
	
	public MilestonesManager registerMilestoneCommand() {
		CommandsManager.getHandler().register(new MilestoneCommand(this.milestones));
		return this;
	}
}
