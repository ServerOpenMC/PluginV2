package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.loots.CreakingHeart;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;

public class CreakingHeartQuest extends MilestoneQuest implements Listener {
	
	public CreakingHeartQuest() {
		super(
				"La résine n'a pas de coeur",
				List.of(
						"§fRécupérer un §dCoeur de Creaking",
						"§8§oOn cherche la résine ou le coeur ?"
				),
				Material.RESIN_CLUMP,
				MilestoneType.DREAM,
				DreamSteps.GET_HEART,
				new QuestTier(1),
				List.of(
						"§6Ah ! Mais c'est vrai qu'il protègent leurs coeurs.",
						"§3Voyageur : Oui, et ceux-ci nous seront utiles par la suite, les crafts de ce monde en utilisent beaucoup. " +
								"Que ce soit pour les outils, armures, ou objets divers de ce monde. Commence par te faire une §dhache§3.",
						"§6Mais quel est le but de ce monde ??",
						"§3Voyageur : Je t'expliquerai bientôt, je n'ai pas envie que tu finisses comme moi..."
				)
		);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			DreamItem item = DreamItemRegistry.getByItemStack(e.getItem().getItemStack());
			if (item == null) return;
			if (item instanceof CreakingHeart) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
