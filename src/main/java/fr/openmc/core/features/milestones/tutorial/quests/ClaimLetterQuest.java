package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.mailboxes.events.ClaimLetterEvent;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ClaimLetterQuest extends MilestoneQuest implements Listener {

    public ClaimLetterQuest() {
        super(
                "Ouvrir la lettre des récompenses",
                List.of(
                        "§fTapez §d/mailbox §fou bien allez dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu mailbox",
                        "§8§oUn moyen efficace d'envoyer des items à d'autres joueurs !"
                ),
                Material.PAPER,
		        MilestoneType.TUTORIAL,
		        TutorialStep.CLAIM_LETTER,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.CLAIM_LETTER.ordinal() + 1) + " §f! Vous avez découvert les bases d'OpenMC. Et maintenant, libre à vous d'aller découvrir les features par vous même ! Sur ce, nous vous souhaitons le meilleur de votre aventure sur §dOpenMC §f!",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onClaimLetter(ClaimLetterEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
