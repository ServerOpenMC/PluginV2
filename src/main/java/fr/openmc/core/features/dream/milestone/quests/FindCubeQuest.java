package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.cube.events.EnterCubeZoneEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FindCubeQuest extends MilestoneQuest implements Listener {
	public FindCubeQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.find_cube.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.find_cube.description"),
				Material.LAPIS_BLOCK,
				MilestoneType.DREAM,
				DreamSteps.FIND_CUBE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.find_cube.dialog")
		);
	}
	
	@EventHandler
	public void onEnterCubeZone(EnterCubeZoneEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
