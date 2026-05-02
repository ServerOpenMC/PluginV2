package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.blocks.EternalCampFire;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftEternalFireQuest extends MilestoneQuest implements Listener {
	
	public CraftEternalFireQuest() {
		super(
				"L'ère glacière",
				List.of(
						"§fFabriquer le §dfeu éternel",
						"§8§oVa-t-on retrouver des dinosaures fossilisés ou Sid ?"
				),
				DreamItemRegistry.getByName("omc_dream:eternal_campfire").getBest(),
				MilestoneType.DREAM,
				DreamSteps.CRAFT_ETERNAL_FIRE,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Bien ! Ce feu te sera utile.",
						"§6Il s'agit d'un feu, ça va me permettre de cuire quelque chose ?",
						"§3Voyageur : Dans ce monde, ce feu te servira à limiter les §deffets du froid §3des profondeurs. Tu vas rentrer dans un monde glacial, chaque seconde passée " +
								"dans celui-ci §dte ralentira §3et finira par §dte tuer§3.",
						"§3Voyageur : En posant ce feu au sol et en restant proche de lui, tu vas te réchauffer et éviter cela.",
						"§6Comme lorsqu'on sort de la poudreuse.",
						"§3Voyageur : Oui. Maintenant que tu as compris le principe, partons à la recherche des §dcamps §3présents dans les §dgrottes des profondeurs§3."
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if (e.getWhoClicked() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			ItemStack item = e.getCurrentItem();
			if (item == null) return;
			
			DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
			if (dreamItem == null) return;
			if (dreamItem instanceof EternalCampFire) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
