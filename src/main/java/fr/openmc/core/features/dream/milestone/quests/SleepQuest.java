package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.DreamEnterEvent;
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

public class SleepQuest extends MilestoneQuest implements Listener {
	
	public SleepQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.sleep.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.sleep.description"),
				Material.RED_BED,
				MilestoneType.DREAM,
				DreamSteps.SLEEP,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.sleep.dialog")
		);
	}
	
	@EventHandler
	public void onDreamEnter(DreamEnterEvent e) {
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		
		this.incrementProgressInDream(player.getUniqueId());
	}
}
