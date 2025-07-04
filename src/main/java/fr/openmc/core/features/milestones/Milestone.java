package fr.openmc.core.features.milestones;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.quests.objects.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Milestone {
	HashMap<UUID, MilestoneModel> playerData = new HashMap<>();

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
	List<Quest> getSteps();

	/**
	 * Returns the Type of the Milestone
	 *
	 * @return A step list of the milestone.
	 */
	MilestoneType getType();

	Menu getMenu(Player player);
}
