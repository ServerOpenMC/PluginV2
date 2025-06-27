package fr.openmc.core.features.milestones;

import fr.openmc.core.features.quests.objects.Quest;
import org.bukkit.Material;

import java.util.List;

public abstract class Milestone {
	
	/**
	 * Returns the name of the milestone.
	 *
	 * @return The name of the milestone.
	 */
	public abstract String getName();
	
	/**
	 * Returns the description of the milestone.
	 *
	 * @return The description of the milestone.
	 */
	public abstract String getDescription();
	
	/**
	 * Returns the icon of the milestone.
	 *
	 * @return The icon of the milestone.
	 */
	public abstract Material getIcon();
	
	/**
	 * Returns the steps of the milestone.
	 *
	 * @return A step list of the milestone.
	 */
	public abstract List<Quest> getSteps(); //TODO a voir si on met des quests ou des advencements
}
