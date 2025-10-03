package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.api.menulib.events.OpenMenuEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.menu.ContributionMenu;
import fr.openmc.core.features.contest.menu.TradeMenu;
import fr.openmc.core.features.contest.menu.VoteMenu;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OpenContestMenuQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public OpenContestMenuQuest() {
        super(
                "Ouvrez le menu des Contests",
                List.of(
                        "§fTapez §d/contest §fou bien aller dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu",
                        "§8§oUne méthode compétitive pour gagner des grosses récompenses !"
                ),
                CustomItemRegistry.getByName("omc_contest:contest_shell").getBest()
        );

        this.step = TutorialStep.OPEN_CONTEST;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(1000),
                new QuestTextReward(
                        "Bien Joué ! Vous avez fini l'§6Étape " + (step.ordinal() + 1) + " §f! Les §6Contests§f opposent 2 groupes sur un thème, les gagnants remportent une grosse récompense ! Et voila le tutoriel est maintenant terminé, allez récupérer votre récompense dans la §9Mailbox§f, un système de lettre pour recevoir ou bien envoyer des lettres ! Sur ce, nous vous souhaitons le meilleur de votre aventure sur §dOpenMC §f!",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> {
                            TutorialUtils.completeStep(type, player, step);

                            List<ItemStack> items = new ArrayList<>();
                            ItemStack aywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
                            aywenite.setAmount(30);
                            items.add(aywenite);

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
                )
        ));
    }

    @EventHandler
    public void onContestCommand(OpenMenuEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getMenu() == null) return;

        if (!(event.getMenu() instanceof ContributionMenu
                || event.getMenu() instanceof TradeMenu
                || event.getMenu() instanceof VoteMenu)
        ) return;

        this.incrementProgress(player.getUniqueId());
    }

}
