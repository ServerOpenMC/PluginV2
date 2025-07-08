package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillZombieQuest extends Quest implements Listener {

    public KillZombieQuest() {
        super("Apocalypse zombie ?", "Tuer {target} zombies", Material.ZOMBIE_HEAD);

        this.addTiers(
                new QuestTier(1000, new QuestMoneyReward(8000)),
                new QuestTier(4000, new QuestMoneyReward(10000)),
                new QuestTier(10000, new QuestMoneyReward(15000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onZombieKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player && event.getEntity() instanceof Zombie) {
            this.incrementProgress(player.getUniqueId());
        }
    }

}
