package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.loots.Ewenite;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;

public class EweniteQuest extends MilestoneQuest implements Listener {
	
	public EweniteQuest() {
		super(
				"Aywenite, Awyenito ou Ewenite ?",
				List.of(
						"§fObtenir de l'§dEwenite",
						"§8§oUn nouveau minerai à exploiter.",
						"§8§oBizarre, ce nom m'est familier."
				),
				DreamItemRegistry.getByName("omc_dream:ewenite").getBest(),
				MilestoneType.DREAM,
				DreamSteps.EWENITE,
				new QuestTier(1),
				List.of(
						"§6Le début de la richesse. Qu'il s'agisse de miner ou de les échanger, j'ai l'impression que la recherche va être longue.",
						"§3Voyageur : Le minerai n'est pas commun, mais tu peux en trouver plusieurs rapidement.",
						"§6N'y a-t-il pas moyen d'aller plus vite ?",
						"§3Voyageur : Tu peux te fabriquer la §dPioche mécanique §3si tu le souhaites, ça sera plus rapide."
				)
		);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			DreamItem item = DreamItemRegistry.getByItemStack(e.getItem().getItemStack());
			if (item == null) return;
			if (item instanceof Ewenite) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void onTrade(GlaciteTradeEvent e) {
		Player player = e.getPlayer();
		if (e.getTrade().getResult() instanceof Ewenite) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
