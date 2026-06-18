package fr.openmc.core.features.milestones.tutorial.quests;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class BreakAyweniteQuest extends MilestoneQuest implements Listener {

    public BreakAyweniteQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.break_aywenite.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.break_aywenite.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.break_aywenite.description.2")
                ),
                OMCRegistry.CUSTOM_ITEMS.AYWENITE,
                MilestoneType.TUTORIAL,
                TutorialSteps.BREAK_AYWENITE,
                new QuestTier(
                        30,
                        new QuestMoneyReward(3500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.break_aywenite.reward",
                                        Component.text(TutorialSteps.BREAK_AYWENITE.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        ),
                        new QuestMethodsReward(
                                player -> {
                                    if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
                                        TutorialSteps.CITY_CREATE.getQuest().incrementProgress(player.getUniqueId());
                                    }
                                }
                        )
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (MilestonesManager.getPlayerStep(type, event.getPlayer()) != step.ordinal()) return;

        if (!ItemsAdderHook.isEnable()) return;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());
        if (customBlock != null && customBlock.getNamespacedID() != null &&
                ("omc_blocks:aywenite_ore".equals(customBlock.getNamespacedID()) ||
                        "omc_blocks:deepslate_aywenite_ore".equals(customBlock.getNamespacedID()))
        ) {
            Player player = event.getPlayer();
            this.incrementProgress(player.getUniqueId());
			this.getType().getMilestone().getPlayerData().get(player.getUniqueId()).incrementProgress();
        }
    }
}
