package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.GlaciteOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class GlaciteOrbQuest extends MilestoneQuest implements Listener {
	
	public GlaciteOrbQuest() {
		super(
				"Enfin la dernière ?",
				List.of(
						"§fEchanger l'§dOrbe de Glace",
						"§8§oDernière ligne droite pour les collecter toutes."
				),
				DreamItemRegistry.getByName("omc_dream:glacite_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.GLACITE_ORB,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Enfin, nous touchons au but. Bien plus qu'une seule étape pour enfin être libéré de cette situation.",
						"§6Que dois-je faire ?",
						"§3Voyageur : Il faut les §dcombiner§3.",
						"§6Les combiner ? Pas d'incantation cette fois ?",
						"§3Voyageur : Non, promis. Combine-les dans la §dtable de craft §3que nous avons confectionné tous les deux au début de notre aventure. " +
								"Nous pourrons ensuite utiliser le pouvoir de la §dsingularité§3."
				)
		);
	}
	
	@EventHandler
	public void onTrade(GlaciteTradeEvent e) {
		Player player = e.getPlayer();
		if (e.getTrade().getResult() instanceof GlaciteOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
