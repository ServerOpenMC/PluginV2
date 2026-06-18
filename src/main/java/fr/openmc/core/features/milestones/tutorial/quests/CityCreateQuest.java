package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.events.MemberJoinEvent;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class CityCreateQuest extends MilestoneQuest implements Listener {

    public CityCreateQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.city_create.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.city_create.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.city_create.description.2")
                ),
                Material.OAK_DOOR,
                MilestoneType.TUTORIAL,
                TutorialSteps.CITY_CREATE,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.city_create.reward",
                                        Component.text(TutorialSteps.CITY_CREATE.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        ),
                        new QuestMethodsReward(
                                player -> {
                                    City playerCity = CityManager.getPlayerCity(player.getUniqueId());
                                    if (playerCity.getLevel() >= 2) {
                                        TutorialSteps.CITY_LEVEL_2.getQuest().incrementProgress(player.getUniqueId());
                                    }
                                }
                        )
                )
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCityCreate(CityCreationEvent event) {
        Player player = event.getOwner();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinCity(MemberJoinEvent event) {
        OfflinePlayer player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player.getUniqueId()) != step.ordinal()) return;

        if (player.isOnline()) {
            this.incrementProgress(player.getUniqueId());
        }
    }
}
