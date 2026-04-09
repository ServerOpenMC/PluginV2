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

public class GrottoCampQuest extends MilestoneQuest implements Listener {
	
	public GrottoCampQuest() {
		super(
				"La survie en mode fin du monde ?",
				List.of(
						"§fTrouver un §dcamp de grotte",
						"§8§oIl n'y a pas de Transperceneige mais,",
						"§8§oy aurait-il, une zone protégée."
				),
				Material.DEEPSLATE,
				MilestoneType.DREAM,
				DreamSteps.GROTTO_CAMP,
				new QuestTier(1),
				List.of(
						"§6Voilà un camp.",
						"§3Voyageur : Voyons si nous pouvons trouver le dernier dépositaire de l'orbe. Il doit être dans les parages.",
						"§6Mais si le Cube à tout corrompu et tué, comment le marchand peut-il être encore vivant ?",
						"§3Voyageur : L'orbe, mon ami. Avant, il s'agissait d'un marchand ambulant qui parcourait le monde pour échanger ses marchandises.",
						"§3Voyageur : Et chaque orbe possède un pouvoir, et je pense celui de l'§dOrbe de Glace §3a permis à ce marchant de survivre, mais à quel prix..."
				)
		);
	}
	
	@EventHandler
	public void onCastleEnter(PlayerEnterStructureEvent e) {
		if (e.getStructure().type() != DreamStructure.DreamType.BASE_CAMP) return;
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
