package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.MetalDetector;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MetalDetectorQuest extends MilestoneQuest implements Listener {
	
	public MetalDetectorQuest() {
		super(
				"Coooooooaaaaaaa",
				List.of(
						"§fFabriquer le détecteur à métaux",
						"§8§oPourquoi les têtards sont aussi gros ?"
				),
				DreamItemRegistry.getByName("omc_dream:metal_detector").getBest(),
				MilestoneType.DREAM,
				DreamSteps.METAL_DETECTOR,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Ah, enfin le détecteur. Nous allons pouvoir rechercher l'orbe dans cette boue plus facilement.",
						"§3Voyageur : Ces plages étaient avant bien de sable et d'eau. Mais avec la catastrophe, les grenouilles et têtards se sont transformés. " +
								"Cette transformation s'est soldée, pour eux, par une grande soif.",
						"§6D'où l'absence d'eau."
				)
		);
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		
		DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
		if (dreamItem == null) return;
		if (dreamItem instanceof MetalDetector) {
			if (e.getWhoClicked() instanceof Player player) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
