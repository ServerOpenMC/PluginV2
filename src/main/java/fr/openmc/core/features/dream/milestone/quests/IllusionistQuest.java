package fr.openmc.core.features.dream.milestone.quests;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IllusionistQuest extends MilestoneQuest implements Listener {
	
	public IllusionistQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.illusionist.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.illusionist.description"),
				ItemUtils.getTexturedItem(Material.PILLAGER_SPAWN_EGG),
				MilestoneType.DREAM,
				DreamSteps.ILLUSIONIST,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.illusionist.dialog")
		);
	}
	
	@EventHandler
	public void onInterract(NpcInteractEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (!e.getNpc().getData().getName().startsWith("glacite-")) return;
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
