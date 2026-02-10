package fr.openmc.core.features.milestones.tutorial.quests;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialBossBar;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class BreakAyweniteQuest extends MilestoneQuest implements Listener {

    public BreakAyweniteQuest() {
        super(
                "Casser 30 §dAywenites",
                List.of(
                        "§fLe nouveau minerai de la §dV2, trouvable dans les grottes",
                        "§fIl vous sera §dutile §fdans de nombreuses fonctionnalités"
                ),
                CustomItemRegistry.getByName("omc_items:aywenite").getBest(),
		        MilestoneType.TUTORIAL,
		        TutorialStep.BREAK_AYWENITE,
		        new QuestTier(
				        30,
				        new QuestMoneyReward(3500),
				        new QuestTextReward("Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.BREAK_AYWENITE.ordinal() + 1 /* Pas le choix */) + " §f! Comme dit précédemment l'§dAywenite §fest un minerai, précieux pour les features. D'ailleurs vous pouvez l'utiliser pour faire votre ville ! ", Prefix.MILLESTONE, MessageType.SUCCESS),
				        new QuestMethodsReward(
						        player -> {
							        if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
								        TutorialStep.CITY_CREATE.getQuest().incrementProgress(player.getUniqueId());
							        }
						        }
				        )
		        )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (MilestonesManager.getPlayerStep(type, event.getPlayer()) != step.ordinal()) return;

        if (!ItemsAdderHook.isHasItemAdder()) return;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());
        if (customBlock != null && customBlock.getNamespacedID() != null &&
                ("omc_blocks:aywenite_ore".equals(customBlock.getNamespacedID()) ||
                        "omc_blocks:deepslate_aywenite_ore".equals(customBlock.getNamespacedID()))
        ) {
            Player player = event.getPlayer();
            this.incrementProgress(player.getUniqueId());
			getType().getMilestone().getPlayerData().get(player.getUniqueId()).incrementProgress();

            int progress = this.getProgress(player.getUniqueId());
			
            if (progress >= 30) return;
            TutorialBossBar.update(
                    player,
                    Component.text(TutorialBossBar.PLACEHOLDER_TUTORIAL_BOSSBAR.formatted(
                            (step.ordinal() + 1),
                            TutorialStep.values()[step.ordinal()].getQuest().getName(player.getUniqueId()) + " (" + progress + " / 30)"
                    )),
                    (float) this.getProgress(player.getUniqueId()) / 30
            );
        }
    }
}
