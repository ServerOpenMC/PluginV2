package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.MetalDetector;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class MetalDetectorQuest extends MilestoneQuest implements Listener {
	
	public MetalDetectorQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.metal_detector.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.metal_detector.description"),
				DreamItemRegistry.METAL_DETECTOR,
				MilestoneType.DREAM,
				DreamSteps.METAL_DETECTOR,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.metal_detector.dialog")
		);
	}
	
	@EventHandler
	public void onCollectDetector(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (! DreamUtils.isInDreamWorld(player)) return;
			
			ItemStack baseItem = e.getItem().getItemStack();
			
			DreamItem item = DreamItemRegistry.getByItemStack(baseItem);
			if (item == null) return;
			if (item instanceof MetalDetector) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId(), baseItem.getAmount());
			}
		}
	}
}
