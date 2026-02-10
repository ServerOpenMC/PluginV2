package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.events.CityUpgradeEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class CityLevelTwoQuest extends MilestoneQuest implements Listener {
    
    public CityLevelTwoQuest() {
        super(
                "Avoir une ville niveau 2",
                List.of(
                        "§fFaites §d/city milestone §fpour en savoir plus comment",
                        "§faméliorer votre ville !"
                ),
                Material.NETHER_STAR,
		        MilestoneType.TUTORIAL,
		        TutorialStep.CITY_LEVEL_2,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.CITY_LEVEL_2.ordinal() + 1) + " §f! Vous êtes bien parti pour découvrir toutes les features qu'ils se cachent dans les villes ! Mais avant cela, je voudrais que vous posiez un §2home§f.",
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
