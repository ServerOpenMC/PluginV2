package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OpenContestMenuQuest extends MilestoneQuest implements Listener {

    public OpenContestMenuQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.open_contest.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.open_contest.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.open_contest.description.2")
                ),
				OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL,
                MilestoneType.TUTORIAL,
                TutorialSteps.OPEN_CONTEST,
                new QuestTier(
                        1,
                        new QuestMoneyReward(1000),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.open_contest.reward",
                                        Component.text(TutorialSteps.OPEN_CONTEST.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        ),
                        new QuestItemReward(OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest(), 30),
						new QuestMethodsReward(
								player -> {
									List<ItemStack> items = new ArrayList<>();

									FileConfiguration config = OMCPlugin.getConfigs();
									if (config != null) {
										LocalDate today = LocalDate.now();
										LocalDate limitDate = LocalDate.of(
												config.getInt("features.aywen_pelush.year", 2025),
												config.getInt("features.aywen_pelush.month", 11),
												config.getInt("features.aywen_pelush.day", 3)
										);

										if (!limitDate.isBefore(today)) {
											ItemStack aywenPlush = OMCRegistry.CUSTOM_ITEMS.PELUCHE_AWYEN.getBest();
											items.add(aywenPlush);
										}
									}

									ItemStack[] itemsArray = items.toArray(new ItemStack[0]);

									MailboxManager.sendItems(player, player, itemsArray);
								}
						)
                )
        );
    }

    @EventHandler
    public void onContestCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (!event.getMessage().equals("/contest")) return;

        this.incrementProgress(player.getUniqueId());
    }

}
