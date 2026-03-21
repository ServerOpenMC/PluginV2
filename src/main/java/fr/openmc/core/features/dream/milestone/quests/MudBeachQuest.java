package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.PlayerEnterBiomeEvent;
import fr.openmc.core.features.dream.generation.DreamBiome;
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

public class MudBeachQuest extends MilestoneQuest implements Listener {
	public MudBeachQuest() {
		super(
				"Je préfère la plage",
				List.of(
						"§fEntrer sur la §dPlage de Boue",
						"§8§oProfitons de ce rêve pour aller se dorer la pilule au Soleil.",
						"Tiens, pourquoi n'y a-t-il pas d'eau, mais que de la boue ?",
						"§8§oEt toujours pas de Soleil !? Cet endroit n'est donc que de la nuit ?"
				),
				Material.MUD,
				MilestoneType.DREAM,
				DreamSteps.MUD_BEACH,
				new QuestTier(
						1,
						new QuestTextReward("Bon, assez de repos, il serait temps que je cherche la prochaine orbe.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onEnterBiome(PlayerEnterBiomeEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (!e.getBiome().equals(DreamBiome.MUD_BEACH.getBiome())) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
