package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
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
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;

public class MudOrbQuest extends MilestoneQuest implements Listener {
	
	public MudOrbQuest() {
		super(
				"Bip Biip Biiiiiiip",
				List.of(
						"§gTrouver l'Orbe de Boue",
						"§8§oBon, pas de sable, mais de la boue. Possible que les gens perdent tout de même " +
								"des choses. Ah tiens, 6 blocks vers la droite."
				),
				DreamItemRegistry.getByName("omc_dream:mud_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.MUD_ORB,
				new QuestTier(1),
				List.of(
						"§6Cela me rappelle mes vacances à chercher des trésors sur la plage. Même si là, c'est pour une meilleure cause. Et maintenant que le 4ème orbe est avec nous, que dois-je faire ?",
						"§3Voyageur : Plus qu'un. Comme nous avons fait toute la surface, alors il ne nous reste plus qu'à chercher sous terre. Il faudra bien se préparer, et notamment un bon feu. " +
								"Pour ce qui est du détecteur, tu as dû voir que l'on a obtenu plusieurs choses.",
						"§6Oui, mon inventaire est bien rempli.",
						"§3Voyageur : Tout comme la canne à pêche, tu peux y obtenir divers objets comme les chips, même si certaines sont très rares, des somnifères, " +
								"un livre enchanté différent de celui des nuages, ou encore une pioche qui te sera utile pour la suite... Je vais d'ailleurs check si tu n'en as pas déjà une."
				),
				player -> {
					if (player.getInventory().contains(DreamItemRegistry.getByName("omc_dream:crystallized_pickaxe").getBest()))
						DreamSteps.CRYSTALLIZED_PICKAXE.getQuest().incrementProgressInDream(player.getUniqueId());
				}
		);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			DreamItem item = DreamItemRegistry.getByItemStack(e.getItem().getItemStack());
			if (item == null) return;
			if (item instanceof MudOrb) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
