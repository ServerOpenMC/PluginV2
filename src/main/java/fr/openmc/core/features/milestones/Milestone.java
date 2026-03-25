package fr.openmc.core.features.milestones;

import fr.openmc.api.menulib.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Milestone {
	HashMap<UUID, MilestoneModel> playerData = new HashMap<>();

	/**
	 * Returns the player data for the milestone.
	 * This is a static method that returns a HashMap containing player UUIDs and their corresponding MilestoneModel.
	 *
	 * @return A HashMap containing player UUIDs and their MilestoneModel.
	 */
	default HashMap<UUID, MilestoneModel> getPlayerData() {
		return playerData;
	}

	/**
	 * Returns the name of the milestone.
	 *
	 * @return The name of the milestone.
	 */
	String getName();
	
	/**
	 * Returns the description of the milestone.
	 *
	 * @return The description of the milestone.
	 */
	List<Component> getDescription();
	
	/**
	 * Returns the icon of the milestone.
	 *
	 * @return The icon of the milestone.
	 */
	ItemStack getIcon();
	
	/**
	 * Returns the steps of the milestone.
	 *
	 * @return A step list of the milestone.
	 */
	List<MilestoneQuest> getSteps();

	/**
	 * Returns the Type of the Milestone
	 *
	 * @return A step list of the milestone.
	 */
	MilestoneType getType();

	/**
	 * Returns the menu associated with the milestone for the given player.
	 *
	 * @param player The player for whom the menu is created.
	 * @return The menu for the milestone.
	 */
	Menu getMenu(Player player);
}
