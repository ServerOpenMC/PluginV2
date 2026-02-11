package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.events.DreamEnterEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SleepQuest extends MilestoneQuest implements Listener {
	
	public SleepQuest() {
		super(
				"Dormir, c'est la vie",
				List.of(
						"§fEntrer dans la §ddimension des rêves",
						"§8§oQue c'est bon de dormir pour se reposer de la dure vie des villes..."
				),
				Material.RED_BED,
				MilestoneType.DREAM,
				DreamSteps.SLEEP,
				new QuestTier(
						1,
						new QuestTextReward("ZZZzzz... que se passe-t-il ? Je vois un monde sombre rempli de créatures étranges. Suis-je en train de rêver ? " +
								"Ce monde est si différent de l'overworld, il faut que je m'adapte et que je comprenne ce monde.\n" +
								"§d/milestone §opour voir la suite du guide", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onDreamEnter(DreamEnterEvent e) {
		Player player = e.getPlayer();
		
		if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) != DreamSteps.SLEEP.ordinal()) return;
		
		this.incrementProgressInDream(player.getUniqueId());
	}
}
