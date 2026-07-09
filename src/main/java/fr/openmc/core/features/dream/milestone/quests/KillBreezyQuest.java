package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.DreamStructure;
import fr.openmc.core.features.dream.registries.items.orb.CloudOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class KillBreezyQuest extends MilestoneQuest implements Listener {
	public KillBreezyQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.kill_breezy.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.kill_breezy.description"),
				Material.WIND_CHARGE,
				MilestoneType.DREAM,
				DreamSteps.KILL_BREEZY,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.kill_breezy.dialog",
						Component.text(((DreamEquipableItem) DreamItemRegistry.CLOUD_CHESTPLATE).getAdditionalMaxTime()).color(NamedTextColor.LIGHT_PURPLE)
				)
		);
	}
	
	@EventHandler
	public void onCollectOrb(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			if (!DreamStructure.isInInsideDreamStructure(player.getLocation(), DreamStructure.CLOUD_CASTLE)) return;
			
			ItemStack baseItem = e.getItem().getItemStack();
			
			DreamItem item = DreamItemRegistry.getByItemStack(baseItem);
			if (item == null) return;
			if (item instanceof CloudOrb) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId(), baseItem.getAmount());
			}
		}
	}
}
