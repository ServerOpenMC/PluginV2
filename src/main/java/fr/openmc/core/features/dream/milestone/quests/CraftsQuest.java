package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CraftsQuest extends MilestoneQuest implements Listener {
	public CraftsQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.crafts.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.crafts.description"),
				Material.BOOK,
				MilestoneType.DREAM,
				DreamSteps.CRAFTS,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.crafts.dialog",
						TranslationManager.translation(DreamBiome.SCULK_PLAINS.getNameKey()).color(NamedTextColor.LIGHT_PURPLE)
				)
		);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String s = e.getMessage();
		if (!s.equals("/crafts")) return;
		
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
