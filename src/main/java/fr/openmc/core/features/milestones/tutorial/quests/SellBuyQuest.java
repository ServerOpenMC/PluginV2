package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.adminshop.events.BuyEvent;
import fr.openmc.core.features.adminshop.events.SellEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.listeners.PlayerDeathListener;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SellBuyQuest extends MilestoneQuest implements Listener {

    public SellBuyQuest() {
        super(
                "Acheter ou vendre une ressource à l'adminshop",
                List.of(
                        "§fTapez §c/adminshop §fou bien allez dans le §dmenu principal /menu §fpour pouvoir vendre ou acheter une ressource",
                        "§8§oC'est le début de la richesse !"
                ),
                Material.GOLD_INGOT,
		        MilestoneType.TUTORIAL,
		        TutorialStep.SELL_BUY_ADMINSHOP,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.SELL_BUY_ADMINSHOP.ordinal() + 1) + " §f! L'§cadminshop §fpropose divers objets afin de pouvoir build, ou faire de l'argent ! Cependant, lorsque vous mourrez vous perdez §6" + PlayerDeathListener.LOSS_MONEY * 100 + "%§f de votre argent ! Il est donc important de faire attention à votre peau, ou alors de déposer de l'argent dans votre banque !",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onAdminShopSell(SellEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

    @EventHandler
    public void onAdminShopBuy(BuyEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

}
