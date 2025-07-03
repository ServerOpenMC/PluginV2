package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.contest.menu.ContributionMenu;
import fr.openmc.core.features.contest.menu.VoteMenu;
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
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class ClaimLetterQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public ClaimLetterQuest() {
        super(
                "Ouvrez la lettre des Récompenses",
                List.of(
                        "Tapez §d/contest §fou bien aller dans le §dmenu principal /menu §fpour pouvoir ouvrir le menu",
                        "§8§oune méthode compétitive pour gagner des grosses récompenses !"
                ),
                CustomItemRegistry.getByName("omc_contest:contest_shell").getBest()
        );

        this.step = TutorialStep.CLAIM_LETTER;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué! Vous avez fini l'§6Etape " + (step.ordinal() + 1) + " §f! Les §6Contests §f, ça oppose 2 groupes, 2 thèmes opposés, et le gagnant remporte une grosse récompense ! Et voila le tutoriel est maintenant terminé, allez récupérer votre récompense dans la §1Mailbox§f,un système de lettre pour recevoir ou bien envoyer des lettres!",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onClaimLetter(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getInventory().getHolder() == null) return;

        if (!event.getInventory().getHolder().getClass().equals(VoteMenu.class) || !event.getInventory().getHolder().getClass().equals(ContributionMenu.class))
            return;

        this.incrementProgress(player.getUniqueId());
    }

}
