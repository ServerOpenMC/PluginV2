package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class OpenContestMenuQuest extends MilestoneQuest implements Listener {

    public OpenContestMenuQuest() {
        super(
                "Ouvrez le menu des contests",
                List.of(
                        "§fTapez §d/contest §fou bien aller dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu",
                        "§8§oUne méthode compétitive pour gagner des grosses récompenses !"
                ),
                CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(),
		        MilestoneType.TUTORIAL,
		        TutorialStep.OPEN_CONTEST,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(1000),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.OPEN_CONTEST.ordinal() + 1) + " §f! Les §6contests§f opposent 2 groupes sur un thème, les gagnants remportent une grosse récompense ! Et voila le tutoriel est maintenant terminé, allez récupérer votre récompense dans la §9mailbox§f, un système de lettre pour recevoir ou bien envoyer des lettres !",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        ),
						new QuestItemReward(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 30)
				        /* new QuestMethodsReward(
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
									        ItemStack aywenPlush = CustomItemRegistry.getByName("omc_plush:peluche_awyen").getBest();
									        items.add(aywenPlush);
								        }
							        }
							        
							        ItemStack[] itemsArray = items.toArray(new ItemStack[0]);
							        
							        MailboxManager.sendItems(player, player, itemsArray);
						        }
				        ) */ // => PELUCHES AVANT LE 3 NOVEMBRE 2025
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
