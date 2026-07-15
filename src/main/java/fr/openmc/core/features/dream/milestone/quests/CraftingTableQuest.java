package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Recipe;

public class CraftingTableQuest extends MilestoneQuest implements Listener {
	public CraftingTableQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.crafting_table.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.crafting_table.description"),
				Material.CRAFTING_TABLE,
				MilestoneType.DREAM,
				DreamSteps.CRAFTING_TABLE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.crafting_table.dialog")
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
