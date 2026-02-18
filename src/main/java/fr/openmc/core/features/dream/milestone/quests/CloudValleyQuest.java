package fr.openmc.core.features.dream.milestone.quests;

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
				new QuestTier(
						1,
						new QuestTextReward("Ces nuages sont comme une nouvelle plaine, on peut courir dessus, sauter de nuage en nuage, et si on tombe, ça ne fait même pas mal.\n" +
								"Tiens, c'est quoi ça, au loin ? Le château que j'ai cru avoir aperçu tout à l'heure ?", Prefix.DREAM, MessageType.SUCCESS)
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
