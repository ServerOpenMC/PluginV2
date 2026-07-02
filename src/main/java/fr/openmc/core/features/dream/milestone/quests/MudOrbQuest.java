package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.MudOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MudOrbQuest extends MilestoneQuest implements Listener {
	
	public MudOrbQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.mud_orb.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.mud_orb.description"),
				DreamItemRegistry.MUD_ORB,
				MilestoneType.DREAM,
				DreamSteps.MUD_ORB,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.mud_orb.dialog"),
				player -> {
					if (player.getInventory().contains(DreamItemRegistry.CRYSTALIZED_PICKAXE.getBest()))
						DreamSteps.CRYSTALLIZED_PICKAXE.getQuest().incrementProgressInDream(player.getUniqueId());
				}
		);
	}
	
	@EventHandler
	public void onGetOrb(MetalDetectorLootEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;

		for (CustomLoot loot : e.getLoot()) {
			if (!(loot instanceof ItemLoot itemLoot)) continue;

			for (ItemStack item : itemLoot.getItems()) {
				DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
				if (dreamItem == null) return;
				if (dreamItem instanceof MudOrb) {
					if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
					this.incrementProgressInDream(player.getUniqueId());
				}
			}
		}
	}
}
