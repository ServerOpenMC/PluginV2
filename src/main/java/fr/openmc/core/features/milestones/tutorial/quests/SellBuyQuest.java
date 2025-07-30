package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.adminshop.events.BuyEvent;
import fr.openmc.core.features.adminshop.events.SellEvent;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
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

public class SellBuyQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public SellBuyQuest() {
        super(
                "Acheter ou vendre une ressource à l'Admin Shop",
                List.of(
                        "§fTapez §c/adminshop §fou bien allez dans le §dmenu principal /menu §fpour pouvoir vendre ou acheter une ressource",
                        "§8§oC'est le début de la richesse!"
                ),
                Material.GOLD_INGOT
        );

        this.step = TutorialStep.SELL_BUY_ADMINSHOP;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué ! Vous avez fini l'§6Étape " + (step.ordinal() + 1) + " §f! L'§cAdmin Shop §fpropose divers objets afin de pouvoir build, ou faire de l'argent ! Cependant, lorsque vous mourrez vous perdez §6" + PlayerDeathListener.LOSS_MONEY * 100 + "%§f de votre argent ! Il est donc important de faire attention à votre peau, ou alors de déposer de l'argent dans votre banque !",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
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
