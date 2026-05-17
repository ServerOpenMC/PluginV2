package fr.openmc.core.features.milestones.quests;

import fr.openmc.core.features.milestones.MilestoneStep;
import fr.openmc.core.features.milestones.MilestoneUtils;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class MilestoneQuest extends Quest {
	
	protected final MilestoneType type;
	protected final Enum<? extends MilestoneStep> step;
    protected final Consumer<Player> actionsAfterDialog;
    protected List<String> dialogs;

	/**
	 * Constructeur initial de MilestoneQuest
	 * @param name le nom de la quete
	 * @param baseDescription la description de base de la quete
	 * @param icon l'icone de la quete (item stack)
	 * @param type le type de milestone
	 * @param step l'enum lié a l'étape
	 * @param quest les tiers afin de valider la quete
	 */
	public MilestoneQuest(String name, List<String> baseDescription, ItemStack icon, MilestoneType type, Enum<? extends MilestoneStep> step, QuestTier quest) {
		super(name, baseDescription, icon);
		this.type = type;
		this.step = step;
		this.addTier(quest.addReward(
				new QuestMethodsReward(player -> MilestoneUtils.completeStep(type, player, step))
		));
		this.actionsAfterDialog = null;
	}

	/**
	 * Constructeur de MilestoneQuest
	 * @param name le nom de la quete
	 * @param baseDescription la description de base de la quete
	 * @param icon l'icone de la quete (Material)
	 * @param type le type de milestone
	 * @param step l'enum lié a l'étape
	 * @param quest les tiers afin de valider la quete
	 */
	public MilestoneQuest(String name, List<String> baseDescription, Material icon, MilestoneType type, Enum<? extends MilestoneStep> step, QuestTier quest) {
		this(name, baseDescription, new ItemStack(icon), type, step, quest);
	}

	/**
	 * Constructeur de MilestoneQuest
	 * @param name le nom de la quete
	 * @param baseDescription la description de base de la quete
	 * @param icon l'icone de la quete (Material)
	 * @param type le type de milestone
	 * @param step l'enum lié a l'étape
	 * @param quest les tiers afin de valider la quete
	 * @param dialogs les dialogues à afficher lors de la validation de la quête
	 */
	public MilestoneQuest(String name, List<String> baseDescription, Material icon, MilestoneType type, Enum<? extends MilestoneStep> step, QuestTier quest, List<String> dialogs) {
		this(name, baseDescription, new ItemStack(icon), type, step, quest, dialogs);
	}

	/**
	 * Constructeur de MilestoneQuest
	 * @param name le nom de la quete
	 * @param baseDescription la description de base de la quete
	 * @param icon l'icone de la quete (ItemStack)
	 * @param type le type de milestone
	 * @param step l'enum lié a l'étape
	 * @param quest les tiers afin de valider la quete
	 * @param dialogs les dialogues à afficher lors de la validation de la quête
	 */
	public MilestoneQuest(String name, List<String> baseDescription, ItemStack icon, MilestoneType type, Enum<? extends MilestoneStep> step, QuestTier quest, List<String> dialogs) {
		this(name, baseDescription, new ItemStack(icon), type, step, quest, dialogs, null);
	}

	/**
	 * Constructeur de MilestoneQuest
	 * @param name le nom de la quete
	 * @param baseDescription la description de base de la quete
	 * @param icon l'icone de la quete (Material)
	 * @param type le type de milestone
	 * @param step l'enum lié a l'étape
	 * @param quest les tiers afin de valider la quete
	 * @param dialogs les dialogues à afficher lors de la validation de la quête
	 * @param actionsAfterDialog les actions à effectuer après la fin du dialogue
	 */
	public MilestoneQuest(String name, List<String> baseDescription, Material icon, MilestoneType type, Enum<? extends MilestoneStep> step, QuestTier quest, List<String> dialogs, Consumer<Player> actionsAfterDialog) {
		this(name, baseDescription, new ItemStack(icon), type, step, quest, dialogs, actionsAfterDialog);
	}

	/**
	 * Constructeur de MilestoneQuest
	 * @param name le nom de la quete
	 * @param baseDescription la description de base de la quete
	 * @param icon l'icone de la quete (ItemStack)
	 * @param type le type de milestone
	 * @param step l'enum lié a l'étape
	 * @param quest les tiers afin de valider la quete
	 * @param dialogs les dialogues à afficher lors de la validation de la quête
	 * @param actionsAfterDialog les actions à effectuer après la fin du dialogue
	 */
	public MilestoneQuest(String name, List<String> baseDescription, ItemStack icon, MilestoneType type, Enum<? extends MilestoneStep> step, QuestTier quest, List<String> dialogs, Consumer<Player> actionsAfterDialog) {
		super(name, baseDescription, icon);
		this.type = type;
		this.step = step;
		this.dialogs = dialogs;
		this.addTier(quest.addRewards(
				new QuestMethodsReward(player -> MilestoneUtils.completeStep(type, player, step)),
				new QuestDialogReward(step, dialogs)
				)
		);
		this.actionsAfterDialog = actionsAfterDialog;
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
