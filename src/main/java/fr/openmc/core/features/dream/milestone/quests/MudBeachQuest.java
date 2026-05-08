package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.PlayerEnterBiomeEvent;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
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
						"§fEntrer sur la §d" + DreamBiome.MUD_BEACH.getName().substring(2),
						"§8§oProfitons de ce rêve pour aller se dorer la pilule au Soleil.",
						"§8§oTiens, pourquoi n'y a-t-il pas d'eau, mais que de la boue ?",
						"§8§oEt toujours pas de Soleil !? Cet endroit n'est donc que de la nuit ?"
				),
				Material.MUD,
				MilestoneType.DREAM,
				DreamSteps.MUD_BEACH,
				new QuestTier(1),
				List.of(
						"§6Enfin sur la plage, et en effet, il n'y a ni sable chaud, ni soleil rayonnant, juste de la boue et... rien d'autre en fait.",
						"§3Voyageur : Très bien, pose-toi là, je te dois des explications.",
						"§3Voyageur : Ce monde, a subi une §dcatastrophe§3, d'où son apparence. Auparavant, il était si... si seulement je pouvais le revoir.",
						"§6Tu m'as parlé de cinq orbes. Ceux-ci permettraient-ils de faire revenir l'ancien monde, avec un pouvoir de terraformation ?",
						"§3Voyageur : Non, c'est bien plus puissant que cela. Ils permettent de créer un §dtrou de ver §3entre cette §ddimension et la tienne§3. Nous pourrons alors " +
								"utiliser une partie de l'§dénergie §3de ta dimension pour §dremonter le temps §3dans celle-ci, et rétablir son équilibre.",
						"§6Utiliser une partie de l'énergie ? Mais, cela est sans risque pour ma dimentsion ??",
						"§3Voyageur : Oui, bien évidemment, sinon je n'en prendrais pas le risque. Chaque bloc de ton monde donnerait une petite perle de son énergie.",
						"§6Quelle énergie ?",
						"§3Voyageur : Nous devons avancer maintenant. Cherche des §dgrenouilles§3, elles nous seront utiles."
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
