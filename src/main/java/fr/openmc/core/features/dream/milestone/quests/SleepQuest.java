package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.DreamEnterEvent;
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

public class SleepQuest extends MilestoneQuest implements Listener {
	
	public SleepQuest() {
		super(
				"Dormir, c'est la vie",
				List.of(
						"§fEntrer dans la §ddimension des rêves",
						"§8§oQue c'est bon de dormir pour",
						"§8§ose reposer de la dure vie des villes..."
				),
				Material.RED_BED,
				MilestoneType.DREAM,
				DreamSteps.SLEEP,
				new QuestTier(1),
				List.of(
						"§6ZZZzzz... que se passe-t-il !? Suis-je en train de rêver ? Ce... monde à l'air si différent de l'Overworld !",
						"§3Voyageur : Que..? Comment est-ce possible ??! Je pensais être le seul ici. Et cela fait si longtemps que je n'ai vu personne...",
						"§6Mais, qui es-tu ? Ou es-tu ? et pourquoi me parles-tu ??",
						"§3Voyageur : Et bien, bienvenue. Mais chaque chose en son temps. Temps qui va d'ailleurs nous manquer, il faut faire vite, je t'expliquerai tout au fur et à mesure. " +
								"Pour l'heure, je te propose de découvrir ce dont tu vas avoir besoin pour découvrir ce monde avec la commande §d/crafts§3.",
						"§3Voyageur : Sache que si tu ne souhaites pas rester jusqu'à la fin du temps, tu peux te réveiller avec la commande §d/leave§3.\n" +
								"A contrario, tu peux revenir ici plus fréquemment en te fabriquant un §dPyjama§3. Cela te permet d'augmenter tes chances de passer dans ce monde en dormant."
				)
		);
	}
	
	@EventHandler
	public void onDreamEnter(DreamEnterEvent e) {
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		
		this.incrementProgressInDream(player.getUniqueId());
	}
}
