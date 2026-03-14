package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.GlaciteOrb;
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

import java.util.List;

public class GlaciteOrbQuest extends MilestoneQuest implements Listener {
	
	public GlaciteOrbQuest() {
		super(
				"Enfin la dernière ?",
				List.of(
						"§fEchanger l'§dOrbe de Glace"
				),
				DreamItemRegistry.getByName("omc_dream:glacite_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.GLACITE_ORB,
				new QuestTier(
						1,
						new QuestTextReward("", Prefix.DREAM, MessageType.SUCCESS)
				)
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
