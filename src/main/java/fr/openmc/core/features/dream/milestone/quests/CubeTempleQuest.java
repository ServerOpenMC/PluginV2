package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
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

public class CubeTempleQuest extends MilestoneQuest implements Listener {
	public CubeTempleQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.cube_temple.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.cube_temple.description",
						DreamStructure.CUBE_TEMPLE.getName().color(NamedTextColor.LIGHT_PURPLE)
				),
				Material.POLISHED_BLACKSTONE_BRICKS,
				MilestoneType.DREAM,
				DreamSteps.CUBE_TEMPLE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.cube_temple.dialog")
		);
	}
	
	@EventHandler
	public void onCastleEnter(PlayerEnterStructureEvent e) {
		if (!e.getStructure().equals(DreamStructure.CUBE_TEMPLE)) return;
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
