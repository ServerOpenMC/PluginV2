package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.contest.ContestEvent;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class WinContestQuest extends Quest {

    public WinContestQuest() {
        super(
                "Choisir son camp",
                "Gagne {target} contest",
                Material.NAUTILUS_SHELL
        );

        this.addTiers(new QuestTier(1, new QuestItemReward(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), 5)));
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEndContest(ContestEvent event) {
        for (Player player : event.getWinners()) {
            this.incrementProgress(player.getUniqueId());
        }
    }
}
