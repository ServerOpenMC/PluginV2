package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.MudOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MudOrbQuest extends MilestoneQuest implements Listener {
	
	public MudOrbQuest() {
		super(
				"Bip Biip Biiiiiiip",
				List.of(
						"§fTrouver l'§dOrbe de Boue",
						"§8§oBon, pas de sable, mais de la boue.",
						"§8§oPossible que les gens perdent tout de même",
						"§8§odes choses. Ah tiens, 6 blocks vers la droite."
				),
				DreamItemRegistry.getByName("omc_dream:mud_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.MUD_ORB,
				new QuestTier(1),
				List.of(
						"§6Cela me rappelle mes vacances à chercher des trésors sur la plage. Même si là, c'est pour une meilleure cause. Et maintenant que le 4ème orbe est avec nous, que dois-je faire ?",
						"§3Voyageur : Plus qu'un. Comme nous avons fait toute la surface et les nuages, alors il ne nous reste plus qu'à chercher §dsous terre§3. Il faudra bien se préparer, et notamment un §dbon feu§3. " +
								"Pour ce qui est du détecteur, tu as dû voir que l'on a obtenu plusieurs choses.",
						"§6Oui, mon inventaire est bien rempli.",
						"§3Voyageur : Tout comme la canne à pêche, tu peux y obtenir divers objets comme les §dchips§3, même si certaines sont très rares, des §dsomnifères§3, " +
								"un autre §dlivre enchanté§3, ou encore une §dpioche §3qui te sera utile pour la suite... Je vais d'ailleurs check si tu n'en as pas déjà une."
				),
				player -> {
					if (player.getInventory().contains(DreamItemRegistry.getByName("omc_dream:crystallized_pickaxe").getBest()))
						DreamSteps.CRYSTALLIZED_PICKAXE.getQuest().incrementProgressInDream(player.getUniqueId());
				}
		);
	}
	
	@EventHandler
	public void onGetOrb(MetalDetectorLootEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = DreamItemRegistry.getByItemStack(e.getLoot().getFirst());
		if (item == null) return;
		if (item instanceof MudOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
