package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.adminshop.events.BuyEvent;
import fr.openmc.core.features.adminshop.events.SellEvent;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.listeners.PlayerDeathListener;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SellBuyQuest extends MilestoneQuest implements Listener {

    public SellBuyQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.sell_buy.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.sell_buy.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.sell_buy.description.2")
                ),
                Material.GOLD_INGOT,
                MilestoneType.TUTORIAL,
                TutorialSteps.SELL_BUY_ADMINSHOP,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.sell_buy.reward",
                                        Component.text(TutorialSteps.SELL_BUY_ADMINSHOP.ordinal() + 1).color(NamedTextColor.GOLD),
                                        Component.text(PlayerDeathListener.LOSS_MONEY * 100).color(NamedTextColor.GOLD)
                                ),
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
