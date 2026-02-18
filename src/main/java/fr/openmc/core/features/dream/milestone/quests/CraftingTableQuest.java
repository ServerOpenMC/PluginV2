package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
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
				new QuestTier(1),
				List.of(
						"§6Bon, maintenant que j'ai la table, cherchons de quoi faire des outils et... " +
								"comment se fait-il qu'il y ait des Craqueurs ici !?",
						"§3Voyageur : Tu as le bon oeil. Ce monde regorge de créatures étonnantes qui ont un point commun : " +
								"elles se nourrissent de §dtemps§3, en te le §dvolant§3.",
						"§6C'est donc cela cette barre là haut ?",
						"§3Voyageur : Oui, et ce temps n'est pas infini, c'est pourquoi nous devons faire vite. " +
								"Profitons d'être au milieu de ces créatures pour récupérer leurs §dcoeurs§3."
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if (e.getWhoClicked() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			Recipe recipe = e.getRecipe();
			if (recipe.getResult().getType() != Material.CRAFTING_TABLE) return;
			
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
