package fr.openmc.core.features.milestones;

import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.menus.MilestoneDialog;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
public class MilestoneQuest extends Quest {
	
	protected final MilestoneType type;
	protected final Enum step;
	
	public MilestoneQuest(String name, List<String> baseDescription, Material icon, MilestoneType type, Enum step, QuestTier quest) {
		this(name, baseDescription, new ItemStack(icon), type, step, quest);
		
	}
	
	public MilestoneQuest(String name, List<String> baseDescription, ItemStack icon, MilestoneType type, Enum step, QuestTier quest) {
		super(name, baseDescription, icon);
		this.type = type;
		this.step = step;
		this.addTier(quest.addReward(
				new QuestMethodsReward(player -> MilestoneUtils.completeStep(type, player, step))
		));
	}
	
	public MilestoneQuest(String name, List<String> baseDescription, Material icon, MilestoneType type, DreamSteps step, QuestTier quest, List<String> dialogs) {
		this(name, baseDescription, new ItemStack(icon), type, step, quest, dialogs);
		
	}
	
	public MilestoneQuest(String name, List<String> baseDescription, ItemStack icon, MilestoneType type, DreamSteps step, QuestTier quest, List<String> dialogs) {
		super(name, baseDescription, icon);
		this.type = type;
		this.step = step;
		this.addTier(quest.addRewards(
				new QuestMethodsReward(player -> MilestoneUtils.completeStep(type, player, step)),
				new QuestMethodsReward(player -> {
					player.closeInventory();
					MilestoneDialog.send(player, step, dialogs);
				})
		));
	}
	
	/**
	 * Increment the progress for the quest for a player authorizing Dream world
	 *
	 * @param playerUUID The UUID of the player
	 */
	public void incrementProgressInDream(UUID playerUUID) {
		incrementProgress(playerUUID, 1, true);
	}
	
	
	/**
	 * Increment the progress of the quest for a player by a specified amount authorizing Dream world.
	 * <p>
	 * This method will check if the quest is fully completed, and if not, it will increase the progress.
	 * @param playerUUID The UUID of the player
	 * @param amount The amount to increment the progress by
	 */
	public void incrementProgressInDream(UUID playerUUID, int amount) {
		incrementProgress(playerUUID, amount, true);
	}
}
