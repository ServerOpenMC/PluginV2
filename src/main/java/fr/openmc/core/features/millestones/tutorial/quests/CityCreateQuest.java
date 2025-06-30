package fr.openmc.core.features.millestones.tutorial.quests;

import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.events.MemberJoinEvent;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class CityCreateQuest extends Quest implements Listener {

    public CityCreateQuest() {
        super(
                "Créer/Rejoindre une ville",
                List.of("Fait §d/city §fpour commencer"),
                Material.OAK_DOOR
        );

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(1000)
        ));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCityCreate(CityCreationEvent event) {
        Player player = event.getOwner();
        if (player != null) this.incrementProgress(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(MemberJoinEvent event) {
        OfflinePlayer player = event.getPlayer();
        if (player.isOnline()) this.incrementProgress(player.getUniqueId());
    }
}
