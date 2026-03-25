package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.events.QuestCompleteEvent;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FinishQuestQuest extends MilestoneQuest implements Listener {

    public FinishQuestQuest() {
        super(
                "Accomplir une quête",
                List.of(
                        "§fAccomplissez une quête de votre choix",
                        "§8§oCela va pouvoir vous donner de l'argent ou certaines ressources !"
                ),
                Material.DIAMOND,
		        MilestoneType.TUTORIAL,
		        TutorialStep.FINISH_QUEST,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.FINISH_QUEST.ordinal() + 1) + " §f! Et voici une §9récompense §f! Pratique, non ? Allons découvrir une autre méthode de production d'argent et de consommation ! Allez dans l'§cadminshop§f, un endroit où plusieurs items sont achetable à des prix variants en fonction l'§coffre et la demande §f!",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Player player = event.getPlayer();

        if (event.getQuest().getClass() == OpenQuestMenuQuest.class) return;

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
