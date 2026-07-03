package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.loots.Soul;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class SoulsQuest extends MilestoneQuest implements Listener {
	
	public static final int SOULS = 20;
	
	public SoulsQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.souls.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.souls.description",
						Component.text(SOULS).color(NamedTextColor.LIGHT_PURPLE)
				),
				DreamItemRegistry.SOUL,
				MilestoneType.DREAM,
				DreamSteps.SOULS,
				new QuestTier(SOULS),
				TranslationManager.translationLore("feature.dream.milestone.quest.souls.dialog",
						Component.text(SOULS).color(NamedTextColor.GOLD)
				)
		);
	}
	
	@EventHandler
	public void onCollectSoul(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			ItemStack baseItem = e.getItem().getItemStack();
			
			DreamItem item = DreamItemRegistry.getByItemStack(baseItem);
			if (item == null) return;
			if (item instanceof Soul) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId(), baseItem.getAmount());
				getType().getMilestone().getPlayerData().get(player.getUniqueId()).incrementProgress(baseItem.getAmount());
			}
		}
	}
}
