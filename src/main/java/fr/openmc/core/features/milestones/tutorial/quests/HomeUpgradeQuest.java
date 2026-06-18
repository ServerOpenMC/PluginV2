package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.events.HomeUpgradeEvent;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class HomeUpgradeQuest extends MilestoneQuest implements Listener {

    public HomeUpgradeQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.home_upgrade.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.home_upgrade.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.home_upgrade.description.2"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.home_upgrade.description.3")
                ),
                OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_UPGRADE,
                MilestoneType.TUTORIAL,
                TutorialSteps.HOME_UPGRADE,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.home_upgrade.reward",
                                        Component.text(TutorialSteps.HOME_UPGRADE.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        )
                )
        );
    }

    @EventHandler
    public void onHomeUpgrade(HomeUpgradeEvent event) {
        Player player = event.getOwner();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
