package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.MudOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MudOrbQuest extends MilestoneQuest implements Listener {
	
	public MudOrbQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.mud_orb.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.mud_orb.description"),
				DreamItemRegistry.MUD_ORB,
				MilestoneType.DREAM,
				DreamSteps.MUD_ORB,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.mud_orb.dialog"),
				player -> {
					if (player.getInventory().contains(DreamItemRegistry.CRYSTALIZED_PICKAXE.getBest()))
						DreamSteps.CRYSTALLIZED_PICKAXE.getQuest().incrementProgressInDream(player.getUniqueId());
				}
		);
	}
	
	@EventHandler
	public void onGetOrb(MetalDetectorLootEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = DreamItemRegistry.getByItemStack(e.getLoot().getFirst());
		if (item == null) return;
		if (item instanceof MudOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
