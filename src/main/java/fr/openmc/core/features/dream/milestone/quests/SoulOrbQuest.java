package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.SoulOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SoulOrbQuest extends MilestoneQuest implements Listener {
	public SoulOrbQuest() {
		super(
				"Il faut un sacrifice !",
				List.of(
						"§fObtenir l'§dOrbe des Âmes",
						"§8§oÂmes dans la poche, permettez-moi, par votre pouvoir,",
						"§8§od'obtenir votre orbe avec le sacrifice de l'Orbe de Domination."
				),
				DreamItemRegistry.getByName("omc_dream:ame_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.SOUL_ORB,
				new QuestTier(1),
				List.of(
						"§6Et de deux ! En revanche, je n'ai plus l'Orbe de Domination...",
						"§3Voyageur : Oui, il te faudra en refaire une. Mais ce n'est pas le plus compliqué.",
						"§3Voyageur : Maintenant que tu as l'Orbe des Âmes, tu vas pouvoir accéder à la §dnouvelle zone§3 qui, tu vas le voir, ne ressemble pas du tout au monde actuel...",
						"§3Voyageur : Il faut monter dans le ciel, dans les §dnuages§3.",
						"§6Peux-tu me parler un peu du but de cette quête ?",
						"§3Voyageur : Les orbes sont au nombre de §d5§3, et chacun d'eux renferme un §dpouvoir§3. Seuls, ils ne servent à rien. Mais ensemble, ils pourraient aider ce monde.",
						"§6Et... toi, tu es qui au fait ?",
						"§3Voyageur : J'étais...",
						"§3Voyageur : Je suis un joueur tout comme toi, mais un joueur de §dce monde§3."
				)
		);
	}
	
	@EventHandler
	public void onSoulOrbCrafting(AltarCraftingEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = e.getCraftedItem();
		if (item == null) return;
		if (item instanceof SoulOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
