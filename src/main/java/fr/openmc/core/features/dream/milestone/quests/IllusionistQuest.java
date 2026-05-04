package fr.openmc.core.features.dream.milestone.quests;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.bukkit.ItemUtils;
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
						"§fTrouver et aller voir le §dmarchand",
						"§8§oS'il y a des feux allumés,",
						"§8§oc'est qu'il y a de la vie dans le coin."
				),
				ItemUtils.getTexturedItem(Material.PILLAGER_SPAWN_EGG),
				MilestoneType.DREAM,
				DreamSteps.ILLUSIONIST,
				new QuestTier(1),
				List.of(
						"§6Voilà le marchand. Il n'a plus l'air de vouloir se déplacer.",
						"§3Voyageur : Comme je l'ai déjà dit, la corruption l'a §dmodifié §3profondément, même si l'orbe l'a §dprotégé §3de la mort.",
						"§3Voyageur : Je ne t'en avais pas parlé jusqu'à présent, mais n'as-tu rien remarqué dans chaque lieu où nous avons trouvé un orbe ?",
						"§6...",
						"§3Voyageur : Les orbes ont §dpréservé §3les vies autour d'eux. Creakings, Araignées, Âmes, Breeze, Phantom, Vagabond, Grenouilles, Têtards, et ici le marchand.",
						"§3Voyageur : Mais à nous de la faire revenir réellement. Alors vas-y, fais l'§déchange §3pour l'Orbe."
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
