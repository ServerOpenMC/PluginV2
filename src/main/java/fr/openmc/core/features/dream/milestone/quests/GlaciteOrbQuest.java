package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.GlaciteOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GlaciteOrbQuest extends MilestoneQuest implements Listener {
	
	public GlaciteOrbQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.glacite_orb.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.glacite_orb.description"),
				DreamItemRegistry.GLACITE_ORB,
				MilestoneType.DREAM,
				DreamSteps.GLACITE_ORB,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.glacite_orb.dialog")
		);
	}
	
	@EventHandler
	public void onTrade(GlaciteTradeEvent e) {
		Player player = e.getPlayer();
		if (e.getTrade().getResult() instanceof GlaciteOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
