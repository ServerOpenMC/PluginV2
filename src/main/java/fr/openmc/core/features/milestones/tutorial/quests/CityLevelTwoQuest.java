package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.events.CityUpgradeEvent;
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
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class CityLevelTwoQuest extends MilestoneQuest implements Listener {
    
    public CityLevelTwoQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.city_level_two.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.city_level_two.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.city_level_two.description.2")
                ),
                Material.NETHER_STAR,
                MilestoneType.TUTORIAL,
                TutorialSteps.CITY_LEVEL_2,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.city_level_two.reward",
                                        Component.text(TutorialSteps.CITY_LEVEL_2.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        )
                )
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCityUpgrade(CityUpgradeEvent event) {
        City city = event.getCity();

        for (UUID memberUUID : city.getMembers()) {
            if (MilestonesManager.getPlayerStep(type, memberUUID) != step.ordinal()) return;

            this.incrementProgress(memberUUID);
        }
    }
}
