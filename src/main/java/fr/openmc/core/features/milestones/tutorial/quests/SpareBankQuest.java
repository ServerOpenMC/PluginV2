package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.economy.events.BankDepositEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SpareBankQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public SpareBankQuest() {
        super(
                "Déposer de l'argent à la banque",
                List.of(
                        "Tapez §d/bank §fou bien aller dans le §dmenu principal /menu §fpour pouvoir ouvrir le menu",
                        "§8§oune méthode compétitive pour gagner des grosses récompenses !"
                ),
                Material.DIAMOND_BLOCK
        );

        this.step = TutorialStep.SPARE_BANK;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué! Vous avez fini l'§6Etape " + (step.ordinal() + 1) + " §f! Les §bBanques§f sert a stocker une quantité infinie d'argent et de la proteger si vous mourrez! Et maintenant configurer un de vos parametres !",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onDepositBank(BankDepositEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());

        if (player == null || !player.isOnline()) return;

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
