package fr.openmc.core.features.quests.quests;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.events.ContestEndEvent;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class WinContestQuest extends Quest implements Listener {

    public WinContestQuest() {
        super(
                TranslationManager.translation("feature.quests.win_contest.name"),
                TranslationManager.translationLore("feature.quests.win_contest.description"),
                Material.NAUTILUS_SHELL
        );
        
        this.addTier(new QuestTier(1, new QuestItemReward(OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL, 5)));
    }
    
    @EventHandler
    public void onEndContest(ContestEndEvent event) {
        for (UUID playerUUID : event.getWinners()) {
            this.incrementProgress(playerUUID);
        }
    }
}
