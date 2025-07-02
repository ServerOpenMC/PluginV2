package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.homes.events.HomeCreateEvent;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class HomeCreateQuest extends Quest implements Listener {

    private final TutorialStep step = TutorialStep.HOME_CREATE;
    private final MilestoneType type = MilestoneType.TUTORIAL;

    public HomeCreateQuest() {
        super(
                "Poser un Home",
                List.of(
                        "Tapez §d/sethome §fpour faire un home",
                        "§8§oC'est très utile d'en faire un pour se téléportez à sa base !"
                ),
                Material.OAK_DOOR
        );

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué! Vous avez fini l'§6Etape 3§f! Les Homes sont souvent utilisé pour pas perdre votre base ! Vous êtes limité à avoir que 1 Home au début. Va falloir penser à les améliorer...",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onHomeCreate(HomeCreateEvent event) {
        Player player = event.getOwner();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (MilestonesManager.getPlayerStep(MilestoneType.TUTORIAL, player) != TutorialStep.HOME_CREATE.ordinal())
            return;

        if (player != null) this.incrementProgress(player.getUniqueId());
    }

}
