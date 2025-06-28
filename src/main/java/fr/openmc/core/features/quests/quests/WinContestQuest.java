package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.contest.ContestEndEvent;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class WinContestQuest extends Quest implements Listener {

    public WinContestQuest() {
        super(
                "Choisir son camp",
                "Gagne {target} contest",
                Material.NAUTILUS_SHELL
        );
        
        this.addTier(new QuestTier(1, new QuestItemReward(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), 5)));
    }
    
    @EventHandler
    public void onEndContest(ContestEndEvent event) {
        for (UUID playerUUID : event.getWinners()) {
            this.incrementProgress(playerUUID);
        }
    }
}
