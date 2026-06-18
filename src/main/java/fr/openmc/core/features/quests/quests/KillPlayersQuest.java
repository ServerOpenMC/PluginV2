package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class KillPlayersQuest extends Quest implements Listener {

    public KillPlayersQuest() {
        super(
                TranslationManager.translationString("feature.quests.kill_players.name"),
                List.of(TranslationManager.translationString("feature.quests.kill_players.description")),
                Material.IRON_SWORD
        );

        this.addTiers(
                new QuestTier(5, new QuestMoneyReward(500)),
                new QuestTier(20, new QuestMoneyReward(2500)),
                new QuestTier(30, new QuestMoneyReward(5000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            this.incrementProgress(player.getUniqueId(), 1);
        }
    }
}
