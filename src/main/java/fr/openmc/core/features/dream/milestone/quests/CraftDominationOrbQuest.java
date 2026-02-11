package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.DominationOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftDominationOrbQuest extends MilestoneQuest implements Listener {
	public CraftDominationOrbQuest() {
		super(
				"Dominer, c'est cool",
				List.of(
						"§fFabriquer l'§dOrbe de Domination",
						"§8§o1 sur 5 pour les dominer tous !"
				),
				DreamItemRegistry.getByName("omc_dream:domination_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.DOMINATION_ORB,
				new QuestTier(
						1,
						new QuestTextReward("Et d'une ! Et cela me donne accès à une nouvelle zone. " +
								"Il faut que je récupère les autres pour avoir accès à l'ensemble de mes rêves.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		
		DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
		if (dreamItem == null) return;
		if (dreamItem instanceof DominationOrb) {
			if (e.getWhoClicked() instanceof Player player) {
				if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) != DreamSteps.DOMINATION_ORB.ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
