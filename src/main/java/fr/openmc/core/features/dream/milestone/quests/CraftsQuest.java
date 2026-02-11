package fr.openmc.core.features.dream.milestone.quests;

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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CraftsQuest extends MilestoneQuest implements Listener {
	public CraftsQuest() {
		super(
				"Apprendre de nouveaux crafts",
				List.of(
						"§fFaire §d/crafts §fpour voir les crafts disponibles",
						"§8§oCette dimension a ses propres règles, je dois les apprendre pour y survivre"
				),
				Material.BOOK,
				MilestoneType.DREAM,
				DreamSteps.CRAFTS,
				new QuestTier(
						1,
						new QuestTextReward("Ce monde sombre et nouveau semble complexe. Mais cela ressemble à une survie normale, non ? " +
								"Alors commençons par les bases, la table de craft.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String s = e.getMessage();
		if (!s.equals("/crafts")) return;
		
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) != DreamSteps.CRAFTS.ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
