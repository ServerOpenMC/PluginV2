package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.blocks.EternalCampFire;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftEternalFireQuest extends MilestoneQuest implements Listener {
	
	public CraftEternalFireQuest() {
		super(
				"L'ère glacière",
				List.of(
						""
				),
				DreamItemRegistry.getByName("omc_dream:eternal_campfire").getBest(),
				MilestoneType.DREAM,
				DreamSteps.ETERNAL_FIRE,
				new QuestTier(
						1,
						new QuestTextReward("", Prefix.OPENMC, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if (e.getWhoClicked() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			ItemStack item = e.getCurrentItem();
			if (item == null) return;
			
			DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
			if (dreamItem == null) return;
			if (dreamItem instanceof EternalCampFire) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
