package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.blocks.EternalCampFire;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftEternalFireQuest extends MilestoneQuest implements Listener {
	
	public CraftEternalFireQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.craft_eternal_fire.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.craft_eternal_fire.description"),
				DreamItemRegistry.ETERNAL_CAMPFIRE,
				MilestoneType.DREAM,
				DreamSteps.CRAFT_ETERNAL_FIRE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.craft_eternal_fire.dialog")
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
