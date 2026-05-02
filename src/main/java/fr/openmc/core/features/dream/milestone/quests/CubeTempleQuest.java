package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
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
						"§fEntrer dans le §d" + DreamStructure.DreamType.SOUL_ALTAR.getName().substring(2),
						"§8§oA la recherche du monument du Cube des Âmes...",
						"§8§oon est malgré tout dans Minecraft, même dans un rêve !"
				),
				Material.POLISHED_BLACKSTONE_BRICKS,
				MilestoneType.DREAM,
				DreamSteps.CUBE_TEMPLE,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Sache qu'il s'agit d'un autel qui a été érigé pour...",
						"§6Pour ?",
						"§3Voyageur : ...",
						"§3Voyageur : Passons, ce n'est pas le moment. Restons sur le fait qu'il s'agit du maître des lieux.",
						"§6Comment ça le maitre des lieux ?",
						"§3Voyageur : Peu importe, maintenant que tu as compris le principe de cette dimension, je vais pouvoir te " +
								"parler de son §dbut §3et de ce que l'on fait ici.",
						"§3Voyageur : Commence déjà par aller au §dcentre du temple§3 et déposer l'orbe."
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
