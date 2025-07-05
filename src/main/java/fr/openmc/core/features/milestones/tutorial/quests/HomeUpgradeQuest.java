package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.homes.events.HomeUpgradeEvent;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class HomeUpgradeQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public HomeUpgradeQuest() {
        super(
                "Améliorer votre limite de Homes",
                List.of(
                        "§fTapez §d/upgradehome §fou bien aller dans le §dmenu des homes (/homes) §fpour pouvoir améliorer votre limite de Homes",
                        "§8§oCela vous permettra d'avoir plus de Homes !"
                ),
                CustomItemRegistry.getByName("omc_homes:omc_homes_icon_upgrade").getBest()
        );

        this.step = TutorialStep.HOME_UPGRADE;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué ! Vous avez fini l'§6Étape " + (step.ordinal() + 1) + "§f ! Les §2homes §fvous seront très utile pour vous déplacer rapidement entre vos bases ! Maintenant, je pense que vous avez besoin de challenges ! Ouvrez le menu des §9quêtes",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onHomeUpgrade(HomeUpgradeEvent event) {
        Player player = event.getOwner();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
