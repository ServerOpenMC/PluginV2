package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Recipe;

import java.util.List;

public class CraftingTableQuest extends MilestoneQuest implements Listener {
	public CraftingTableQuest() {
		super(
				"Une nouvelle survie ?",
				List.of(
						"§fFabriquer une §dtable de craft",
						"§8§oBizarre cette survie de nuit... on ne voit pas en dehors des pleines de sculks"
				),
				Material.CRAFTING_TABLE,
				MilestoneType.DREAM,
				DreamSteps.CRAFTING_TABLE,
				new QuestTier(
						1,
						new QuestTextReward("Bon, maintenant que j'ai la table, cherchons de quoi faire des outils et... " +
								"comment se fait-il qu'il y ait des Creakings ici !?", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		Recipe recipe = e.getRecipe();
		
		if (recipe.getResult().getType() != Material.CRAFTING_TABLE) return;
		
		if (e.getWhoClicked() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) != DreamSteps.CRAFTING_TABLE.ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
