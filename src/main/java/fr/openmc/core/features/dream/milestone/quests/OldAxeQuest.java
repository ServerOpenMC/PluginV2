package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.OldCreakingAxe;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class OldAxeQuest extends MilestoneQuest implements Listener {
	
	public OldAxeQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.old_axe.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.old_axe.description"),
				DreamItemRegistry.OLD_CREAKING_AXE,
				MilestoneType.DREAM,
				DreamSteps.OLD_AXE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.old_axe.dialog",
						Component.text(((DreamEquipableItem) DreamItemRegistry.OLD_CREAKING_CHESTPLATE).getAdditionalMaxTime()).color(NamedTextColor.LIGHT_PURPLE)
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		
		DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
		if (dreamItem == null) return;
		if (dreamItem instanceof OldCreakingAxe) {
			if (e.getWhoClicked() instanceof Player player) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
