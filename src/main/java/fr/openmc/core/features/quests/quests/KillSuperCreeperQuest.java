package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillSuperCreeperQuest extends Quest implements Listener {

    public KillSuperCreeperQuest() {
        super(
                "Le Boss des Creepers",
                "Tuer {target} creepers super chargés",
                Material.CREEPER_HEAD
        );

        this.addTier(new QuestTier(1, new QuestMoneyReward(1000)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null && event.getEntity() instanceof Creeper creeper && creeper.isPowered()) {
            this.incrementProgress(player.getUniqueId());
        }
    }

}
