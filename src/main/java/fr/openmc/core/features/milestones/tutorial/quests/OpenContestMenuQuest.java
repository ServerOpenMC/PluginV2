package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class OpenContestMenuQuest extends MilestoneQuest implements Listener {

    public OpenContestMenuQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.open_contest.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.open_contest.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.open_contest.description.2")
                ),
                CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(),
                MilestoneType.TUTORIAL,
                TutorialSteps.OPEN_CONTEST,
                new QuestTier(
                        1,
                        new QuestMoneyReward(1000),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.open_contest.reward",
                                        Component.text(TutorialSteps.OPEN_CONTEST.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        ),
                        new QuestItemReward(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 30)
                )
        );
    }

    @EventHandler
    public void onContestCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (!event.getMessage().equals("/contest")) return;

        this.incrementProgress(player.getUniqueId());
    }

}
