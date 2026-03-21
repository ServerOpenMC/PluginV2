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
import org.bukkit.event.Listener;

import java.util.List;

public class CubeTempleQuest extends MilestoneQuest implements Listener {
	public CubeTempleQuest() {
		super(
				"Vénérer le Cube des Ämes",
				List.of(
						"§fEntrer dans le §dTemple des Âmes",
						"§8§oA la recherche du monument du Cube des Âmes...",
						"on est malgré tout dans Minecraft, même dans un rêve !"
				),
				Material.POLISHED_BLACKSTONE_BRICKS,
				MilestoneType.DREAM,
				DreamSteps.CUBE_TEMPLE,
				new QuestTier(
						1,
						new QuestTextReward("Wow, c'est grand ! Qui a pu construire une temple pareil ? Et pourquoi ? Je dois le découvrir. \n" +
								"Tiens... il semble y avoir quelque chose en son centre...", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCastleEnter(PlayerEnterStructureEvent e) {
		if (e.getStructure().type() != DreamStructure.DreamType.SOUL_ALTAR) return;
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
