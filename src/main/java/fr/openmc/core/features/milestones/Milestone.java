package fr.openmc.core.features.milestones;

import fr.openmc.core.features.quests.objects.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

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
	public abstract List<Component> getDescription();
	
	/**
	 * Returns the icon of the milestone.
	 *
	 * @return The icon of the milestone.
	 */
	public abstract ItemStack getIcon();
	
	/**
	 * Returns the steps of the milestone.
	 *
	 * @return A step list of the milestone.
	 */
	public abstract List<Quest> getSteps();
}
