package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.economy.events.BankDepositEvent;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SpareBankQuest extends MilestoneQuest implements Listener {

    public SpareBankQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.spare_bank.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.spare_bank.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.spare_bank.description.2")
                ),
                Material.DIAMOND_BLOCK,
                MilestoneType.TUTORIAL,
                TutorialSteps.SPARE_BANK,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.spare_bank.reward",
                                        Component.text(TutorialSteps.SPARE_BANK.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
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
