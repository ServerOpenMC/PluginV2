package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.milestone.DreamSteps;
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

import java.util.List;

public class CloudCastleQuest extends MilestoneQuest {
	public CloudCastleQuest() {
		super(
				"Laputa",
				List.of(
						"§fEntrer dans le §dChâteau des Nuages",
						"§8§oUn nouveau château à conquérir ?"
				),
				Material.QUARTZ_PILLAR,
				MilestoneType.DREAM,
				DreamSteps.CLOUD_CASTLE,
				new QuestTier(
						1,
						new QuestTextReward("Ce monde regorge de choses cachées. Explorons le château en quête de ressources. Et qui dit château, dit donjon, et donc boss...", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCastleEnter(PlayerEnterStructureEvent e) {
		if (e.getStructure().type() != DreamStructure.DreamType.CLOUD_CASTLE) return;
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
