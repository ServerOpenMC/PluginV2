package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.mobs.Breezy;
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
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class KillBreezyQuest extends MilestoneQuest implements Listener {
	public KillBreezyQuest() {
		super(
				"L'air du vent",
				List.of(
						"§fBattre §dBreezy",
						"§8§oOn va dompter un des esprits de la montagne de Poncahontas, ou alors c'est Elsa ?"
				),
				Material.WIND_CHARGE,
				MilestoneType.DREAM,
				DreamSteps.KILL_BREEZY,
				new QuestTier(
						1,
						new QuestTextReward("Difficile ce château, autant de montres au même endroit... " +
								"Retournons sur la terre ferme pour prendre du bon temps.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onKillBreezy(EntityDeathEvent e) {
		if (e.getDamageSource().getCausingEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			if (!DreamStructuresManager.isInsideStructure(player.getLocation(), DreamStructure.DreamType.CLOUD_CASTLE)) return;
			
			if (e.getEntity() instanceof Breezy breezy && breezy.getId().equals("breezy")) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
