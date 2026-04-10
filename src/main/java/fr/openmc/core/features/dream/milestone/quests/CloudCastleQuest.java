package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class CloudCastleQuest extends MilestoneQuest implements Listener {
	public CloudCastleQuest() {
		super(
				"Laputa",
				List.of(
						"§fEntrer dans le §d" + DreamStructure.DreamType.CLOUD_CASTLE.getName().substring(2),
						"§8§oUn nouveau château à conquérir ?"
				),
				Material.QUARTZ_PILLAR,
				MilestoneType.DREAM,
				DreamSteps.CLOUD_CASTLE,
				new QuestTier(1),
				List.of(
						"§6Ce château ne ressemble en rien à ce que je connais. Comment se fait-il qu'il reste en suspension dans les nuages ?",
						"§3Voyageur : Comme dit précédemment, cet ancien peuple a dompté le pouvoir des vents. Mais avec ce qu'il s'est passsé, je pense que le " +
								"pouvoir s'est renfermé sur lui-même pour protéger le trésor.",
						"§6L'Orbe...",
						"§3Voyageur : Exact ! Ce château devrait te rappeler les §dtrial chambers§3 de ton monde.",
						"§6Tu connais donc mon monde ?",
						"§3Voyageur : ..."
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
