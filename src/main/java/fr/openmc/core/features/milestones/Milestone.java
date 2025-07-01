package fr.openmc.core.features.milestones;

import fr.openmc.core.features.quests.objects.Quest;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class Milestone {
	final HashMap<UUID, DBMilestone> playerData = new HashMap<>();

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

	/**
	 * Returns the Type of the Milestone
	 *
	 * @return A step list of the milestone.
	 */
	public abstract MilestoneType getType();

}
