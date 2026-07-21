package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarBindEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.DominationOrb;
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

public class AltarQuest extends MilestoneQuest implements Listener {
	public AltarQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.altar.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.altar.description"),
				Material.ENCHANTING_TABLE,
				MilestoneType.DREAM,
				DreamSteps.ALTAR,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.altar.dialog",
						Component.text(SoulsQuest.SOULS).color(NamedTextColor.LIGHT_PURPLE),
						Component.text(((DreamEquipableItem) DreamItemRegistry.SOUL_CHESTPLATE).getAdditionalMaxTime()).color(NamedTextColor.LIGHT_PURPLE)
				)
		);
	}
	
	@EventHandler
	public void onAltarBind(AltarBindEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = e.getItem();
		if (item == null) return;
		if (item instanceof DominationOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
