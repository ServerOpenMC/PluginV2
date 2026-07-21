package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.TakeFromSingularityEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TransferableObjectQuest extends MilestoneQuest implements Listener {
	
	public TransferableObjectQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.transferable_object.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.transferable_object.description"),
				Material.LAPIS_BLOCK,
				MilestoneType.DREAM,
				DreamSteps.TRANSFERABLE_OBJECT,
				new QuestTier(
						1,
						new QuestTextReward(TranslationManager.translation("feature.dream.milestone.quest.transferable_object.reward"), Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onTakeItem(TakeFromSingularityEvent e) {
		Player player = e.getPlayer();
		if (DreamUtils.isInDreamWorld(player)) return;
		
		ItemStack item = e.getItem();
		if (item == null) return;
		
		DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
		if (dreamItem == null) return;
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
