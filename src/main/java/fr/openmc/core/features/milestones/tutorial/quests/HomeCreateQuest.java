package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.events.HomeCreateEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class HomeCreateQuest extends MilestoneQuest implements Listener {

    public HomeCreateQuest() {
        super(
                "Poser un home",
                List.of(
                        "§fTapez §d/sethome §fen étant là où vous voulez poser votre home",
                        "§8§oC'est très utile d'en faire un pour se téléporter à sa base !"
                ),
                Material.ENDER_PEARL,
		        MilestoneType.TUTORIAL,
		        TutorialStep.HOME_CREATE,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(HomeLimits.LIMIT_1.getPrice()),
				        new QuestItemReward(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), HomeLimits.LIMIT_1.getAyweniteCost()),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.HOME_CREATE.ordinal() + 1) + " §f! Les homes sont souvent utilisés pour pas perdre votre base ! Vous êtes limité à avoir que 1 home au début. Il va falloir penser à les améliorer...",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onHomeCreate(HomeCreateEvent event) {
        Player player = event.getOwner();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }
}
