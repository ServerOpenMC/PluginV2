package fr.openmc.core.features.milestones;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.milestones.bossbar.MilestoneBossBarOptions;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Milestone<T extends Enum<T> & MilestoneStep> {
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
	 * @return The class of the steps for the milestone.
	 */
	Class<T> getStepClass();

	/**
	 * @return The enum constants of the step class for the milestone.
	 */
	default T[] getStepEnum() {
		return this.getStepClass().getEnumConstants();
	}

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

	/**
	 * Returns the boss bar options for the milestone.
	 * If set to null, no boss bar will be displayed for the milestone.
	 *
	 * @return The boss bar options for the milestone.
	 */
	MilestoneBossBarOptions getBossBarOptions();

	/**
	 * Determines whether the boss bar should be displayed for the given player.
	 *
	 * @param player The player for whom to check if the boss bar should be displayed.
	 * @return true if the boss bar should be displayed for the player, false otherwise.
	 */
	boolean shouldDisplayBossBar(Player player);
	/**
	 * Returns the steps of the milestone.
	 *
	 * @return A step list of the milestone.
	 */
	default List<MilestoneQuest> getSteps() {
		T[] enumStep = this.getStepEnum();
		return Arrays.stream(enumStep).map(MilestoneStep::getQuest).toList();
	}
	
}
