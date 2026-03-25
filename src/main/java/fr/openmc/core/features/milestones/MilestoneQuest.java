package fr.openmc.core.features.milestones;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class MilestoneQuest extends Quest {
	
	protected final MilestoneType type;
	protected final Enum step;
	
	public MilestoneQuest(String name, List<String> baseDescription, Material icon, MilestoneType type, Enum step, QuestTier quest) {
		super(name, baseDescription, icon);
		this.type = type;
		this.step = step;
		this.addTier(quest.addReward(
				new QuestMethodsReward(player -> MilestoneUtils.completeStep(type, player, step))
		));
	}
	
	public MilestoneQuest(String name, List<String> baseDescription, ItemStack icon, MilestoneType type, Enum step, QuestTier quest) {
		this(name, baseDescription, icon.getType(), type, step, quest);
	}
}
