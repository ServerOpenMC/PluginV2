package fr.openmc.core.features.dream.milestone.quests;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.features.dream.DreamUtils;
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

public class IllusionistQuest extends MilestoneQuest implements Listener {
	
	public IllusionistQuest() {
		super(
				"Y'a quelqu'un ?",
				List.of(
						"§fTrouver et aller voir l'§dIllusioneur"
				),
				Material.EVOKER_SPAWN_EGG,
				MilestoneType.DREAM,
				DreamSteps.ILLUSIONIST,
				new QuestTier(
						1,
						new QuestTextReward("", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onInterract(NpcInteractEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (!e.getNpc().getData().getName().startsWith("glacite-")) return;
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
