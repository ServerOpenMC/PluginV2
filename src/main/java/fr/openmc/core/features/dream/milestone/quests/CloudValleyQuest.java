package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterBiomeEvent;
import fr.openmc.core.features.dream.generation.DreamBiome;
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

public class CloudValleyQuest extends MilestoneQuest implements Listener {
	public CloudValleyQuest() {
		super(
				"Montée au septième ciel",
				List.of(
						"§fDécouvrir la §dVallée des Nuages",
						"§8§oCes nuages de ce rêve doivent bien cacher quelque chose..."
				),
				Material.SNOW_BLOCK,
				MilestoneType.DREAM,
				DreamSteps.CLOUD_VALLEY,
				new QuestTier(1),
				List.of(
						"§6Nous sommes dans une pleine... des nuages ???",
						"§3Voyageur : Il s'agit d'une pleine d'un ancien peuple ayant dompté les forces du vent.",
						"§3Voyageur : Tu devrais pouvoir y trouver des §drestes de leur civilisation§3.",
						"§6C'est à dire ?",
						"§3Voyageur : Tu as bien trouvé le Temple du Cube non ?"
				)
		);
	}
	
	@EventHandler
	public void onEnterBiome(PlayerEnterBiomeEvent e) {
		Player player = e.getPlayer();
		
		if (!e.getBiome().equals(DreamBiome.CLOUD_LAND.getBiome())) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
