package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayersQuest extends Quest implements Listener {

    public KillPlayersQuest() {
        super(
                "Tueur à gage",
                "Tuer {target} joueurs",
                Material.IRON_SWORD
        );

        this.addTiers(
                new QuestTier(5, new QuestMoneyReward(2500)),
                new QuestTier(20, new QuestMoneyReward(5000)),
                new QuestTier(30, new QuestMoneyReward(10000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            this.incrementProgress(player.getUniqueId(), 1);
        }
    }
}
