package fr.openmc.core.features.milestones.base;

import fr.openmc.api.menulib.Menu;
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
	 * Returns the maximum step of the milestone.
	 *
	 * @return The maximum step of the milestone.
	 */
	public abstract List<Quest> getSteps();
	
	/**
	 * Returns the menu associated with the milestone.
	 *
	 * @return The menu associated with the milestone.
	 */
	public abstract Menu getMenu();
	
	/**
	 * Returns the command associated with the milestone.
	 *
	 * @return The command associated with the milestone.
	 */
	public abstract String getCommand();
	
	/**
	 * Returns the type of the milestone.
	 *
	 * @return The type of the milestone.
	 */
	public abstract MilestoneType getType();
}
