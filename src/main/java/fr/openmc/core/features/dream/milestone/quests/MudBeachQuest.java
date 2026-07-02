package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.PlayerEnterBiomeEvent;
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

public class MudBeachQuest extends MilestoneQuest implements Listener {
	public MudBeachQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.mud_beach.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.mud_beach.description",
						TranslationManager.translation(DreamBiome.MUD_BEACH.getNameKey()).color(NamedTextColor.LIGHT_PURPLE)
				),
				Material.MUD,
				MilestoneType.DREAM,
				DreamSteps.MUD_BEACH,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.mud_beach.dialog")
		);
	}
	
	@EventHandler
	public void onEnterBiome(PlayerEnterBiomeEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (!e.getBiome().equals(DreamBiome.MUD_BEACH.getBiome())) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
