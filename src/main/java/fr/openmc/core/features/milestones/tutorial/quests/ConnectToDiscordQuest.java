package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.features.toor.event.ConnectToDiscordEvent;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ConnectToDiscordQuest extends MilestoneQuest implements Listener {

    public ConnectToDiscordQuest() {
        super(
                TranslationManager.translation("feature.milestones.tutorial.quest.connect_discord.name"),
                TranslationManager.translationLore("feature.milestones.tutorial.quest.connect_discord.description"),
                Material.PAPER,
                MilestoneType.TUTORIAL,
                TutorialSteps.CONNECT_TO_DISCORD,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.connect_discord.reward",
                                        Component.text(TutorialSteps.CONNECT_TO_DISCORD.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        )
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDiscordConnect(ConnectToDiscordEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        if (player == null) return;
        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(event.getPlayerUUID());
    }

}
