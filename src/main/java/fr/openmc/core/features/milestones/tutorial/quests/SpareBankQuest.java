package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.economy.events.BankDepositEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
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

public class SpareBankQuest extends MilestoneQuest implements Listener {

    public SpareBankQuest() {
        super(
                "Déposer de l'argent à la banque",
                List.of(
                        "§fTapez §d/bank §fou bien allez dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu",
                        "§8§oune méthode compétitive pour gagner des grosses récompenses !"
                ),
                Material.DIAMOND_BLOCK,
		        MilestoneType.TUTORIAL,
		        TutorialStep.SPARE_BANK,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.SPARE_BANK.ordinal() + 1) + " §f! Les §bbanques§f peuvent stocker une quantité infinie d'argent et protègent celui-ci si vous mourrez ! " +
								        "Personnalisons maintenant l'expérience de jeu grâce aux paramètres !",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onDepositBank(BankDepositEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());

        if (player == null || !player.isOnline()) return;

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
