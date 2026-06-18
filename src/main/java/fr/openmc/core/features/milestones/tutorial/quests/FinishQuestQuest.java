package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.events.QuestCompleteEvent;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FinishQuestQuest extends MilestoneQuest implements Listener {

    public FinishQuestQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.finish_quest.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.finish_quest.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.finish_quest.description.2")
                ),
                Material.DIAMOND,
                MilestoneType.TUTORIAL,
                TutorialSteps.FINISH_QUEST,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.finish_quest.reward",
                                        Component.text(TutorialSteps.FINISH_QUEST.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
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
