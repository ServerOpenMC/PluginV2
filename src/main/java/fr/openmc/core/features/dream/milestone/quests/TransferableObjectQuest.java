package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.TakeFromSingularityEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TransferableObjectQuest extends MilestoneQuest implements Listener {
	
	public TransferableObjectQuest() {
		super(
				"Ce n'était qu'un rêve ?",
				List.of(
						"§fRécupérer un objet §dtransferable §fdans l'Overworld"
				),
				Material.LAPIS_BLOCK,
				MilestoneType.DREAM,
				DreamSteps.TRANSFERABLE_OBJECT,
				new QuestTier(
						1,
						new QuestTextReward("", Prefix.DREAM, MessageType.SUCCESS)
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
