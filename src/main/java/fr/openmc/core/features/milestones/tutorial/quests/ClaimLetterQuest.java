package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.mailboxes.events.ClaimLetterEvent;
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

public class ClaimLetterQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public ClaimLetterQuest() {
        super(
                "Ouvrir la lettre des Récompenses",
                List.of(
                        "§fTapez §d/mailbox §fou bien allez dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu mailbox",
                        "§8§oUn moyen efficace d'envoyer des items à d'autres joueurs !"
                ),
                Material.PAPER
        );

        this.step = TutorialStep.CLAIM_LETTER;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué ! Vous avez fini l'§6Étape " + (step.ordinal() + 1) + "Et maintenant ? Vous pouvez lier votre compte Discord à votre compte Minecraft afin d'assurer la sécurité de votre compte !",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onClaimLetter(ClaimLetterEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
