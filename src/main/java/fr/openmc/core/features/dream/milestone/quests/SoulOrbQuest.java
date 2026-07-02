package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.SoulOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoulOrbQuest extends MilestoneQuest implements Listener {
	public SoulOrbQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.soul_orb.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.soul_orb.description"),
				DreamItemRegistry.SOUL_ORB,
				MilestoneType.DREAM,
				DreamSteps.SOUL_ORB,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.soul_orb.dialog")
		);
	}
	
	@EventHandler
	public void onSoulOrbCrafting(AltarCraftingEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = e.getCraftedItem();
		if (item == null) return;
		if (item instanceof SoulOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
