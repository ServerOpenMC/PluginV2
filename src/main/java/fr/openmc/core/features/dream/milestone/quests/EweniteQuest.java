package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.loots.Ewenite;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EweniteQuest extends MilestoneQuest implements Listener {
	
	public EweniteQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.ewenite.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.ewenite.description"),
				DreamItemRegistry.EWENITE,
				MilestoneType.DREAM,
				DreamSteps.EWENITE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.ewenite.dialog")
		);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			DreamItem item = DreamItemRegistry.getByItemStack(e.getItem().getItemStack());
			if (item == null) return;
			if (item instanceof Ewenite) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void onTrade(GlaciteTradeEvent e) {
		Player player = e.getPlayer();
		if (e.getTrade().getResult() instanceof Ewenite) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
