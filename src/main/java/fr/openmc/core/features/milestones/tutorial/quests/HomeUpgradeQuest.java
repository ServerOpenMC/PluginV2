package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.homes.events.HomeUpgradeEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class HomeUpgradeQuest extends MilestoneQuest implements Listener {

    public HomeUpgradeQuest() {
        super(
                "Améliorer votre limite de homes",
                List.of(
                        "§fTapez §d/upgradehome §fou bien aller dans le §dmenu des homes (/homes) §fpour pouvoir améliorer votre limite de homes",
                        "§8§oCela vous permettra d'avoir plus de homes !"
                ),
                CustomItemRegistry.getByName("omc_homes:omc_homes_icon_upgrade").getBest(),
		        MilestoneType.TUTORIAL,
		        TutorialStep.HOME_UPGRADE,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.HOME_UPGRADE.ordinal() + 1) + "§f ! Les §2homes §fvous seront très utile pour vous déplacer rapidement entre vos bases ! maintenant, je pense que vous avez besoin de challenges ! Ouvrez le menu des §9quêtes",
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
