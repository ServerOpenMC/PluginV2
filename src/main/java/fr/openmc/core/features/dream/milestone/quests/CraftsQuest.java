package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
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
				new QuestTier(1),
				List.of(
						"§6Cela ressemble à une survie normale, non ?",
						"§3Voyageur : En effet, ce monde a beau être sombre, nouveau et complexe, il n'est pas si différent de l'Overworld. Mais une dimension se rajoute : le §dtemps §3! " +
								"Pour le moment, tu ne peux qu'accéder à la §dPleine de Sculk§3, mais les autres biomes seront bientôt accessibles.",
						"§6Hmmm, cela m'intrigue. Mais tu as dis que l'on manque de temps, alors ne traînons pas. Il me faut une §dtable de craft§6."
				)
		);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String s = e.getMessage();
		if (!s.equals("/crafts")) return;
		
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
