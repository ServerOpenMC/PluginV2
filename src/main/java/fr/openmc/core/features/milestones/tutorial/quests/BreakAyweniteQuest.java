package fr.openmc.core.features.milestones.tutorial.quests;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class BreakAyweniteQuest extends Quest implements Listener {

    private final TutorialStep step = TutorialStep.BREAK_AYWENITE;
    private final MilestoneType type = MilestoneType.TUTORIAL;

    public BreakAyweniteQuest() {
        super(
                "Casser {target} §dAywenite{s}",
                List.of(
                        "Le nouveau minérai de la §dV2, trouvable dans les grottes",
                        "Il vous sera §dutile §fdans plein de fonctionnalité"
                ),
                CustomItemRegistry.getByName("omc_items:aywenite").getBest()
        );

        this.addTier(new QuestTier(
                30,
                new QuestMoneyReward(3500),
                new QuestTextReward("Bien Joué! Vous avez fini l'§6Etape 1 §f! Comme dit précédemment l'§dAywenite §fest un minerai, précieux pour les features. D'ailleurs vous pouvez faire votre ville ! ", Prefix.MILLESTONE, MessageType.SUCCESS),
                new QuestMethodsReward(
                        (player) -> {
                            MilestonesManager.setPlayerStep(type, player, step.ordinal());
                        }
                )
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (MilestonesManager.getPlayerStep(type, event.getPlayer()) != step.ordinal()) return;

        if (!ItemsAdderApi.hasItemAdder())
            return;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());
        if (customBlock != null && customBlock.getNamespacedID() != null &&
                ("omc_blocks:aywenite_ore".equals(customBlock.getNamespacedID()) ||
                        "omc_blocks:deepslate_aywenite_ore".equals(customBlock.getNamespacedID()))
        ) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }
}
