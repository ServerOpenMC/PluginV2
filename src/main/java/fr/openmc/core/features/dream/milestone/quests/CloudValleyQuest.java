package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterBiomeEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamStructure;
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

public class CloudValleyQuest extends MilestoneQuest implements Listener {
	public CloudValleyQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.cloud_valley.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.cloud_valley.description",
						TranslationManager.translation(DreamBiome.CLOUD_LAND.getNameKey()).color(NamedTextColor.LIGHT_PURPLE)
				),
				Material.SNOW_BLOCK,
				MilestoneType.DREAM,
				DreamSteps.CLOUD_VALLEY,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.cloud_valley.dialog",
						DreamStructure.CUBE_TEMPLE.getName().color(NamedTextColor.LIGHT_PURPLE)
				)
		);
	}
	
	@EventHandler
	public void onEnterBiome(PlayerEnterBiomeEvent e) {
		if (!e.getBiome().equals(DreamBiome.CLOUD_LAND.getBiome())) return;
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
