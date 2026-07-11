package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.DreamStructure;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GrottoCampQuest extends MilestoneQuest implements Listener {
	
	public GrottoCampQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.grotto_camp.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.grotto_camp.description"),
				Material.DEEPSLATE,
				MilestoneType.DREAM,
				DreamSteps.GROTTO_CAMP,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.grotto_camp.dialog")
		);
	}
	
	@EventHandler
	public void onCastleEnter(PlayerEnterStructureEvent e) {
		if (!e.getStructure().equals(DreamStructure.BASE_CAMP)) return;
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
