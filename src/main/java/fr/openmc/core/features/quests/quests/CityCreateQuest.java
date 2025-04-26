package fr.openmc.core.features.quests.quests;

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

public class CityCreateQuest extends Quest implements Listener {

    public CityCreateQuest() {
        super(
                "Citoyen",
                "Créer ou rejoins une ville",
                Material.OAK_DOOR
        );

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(3000)
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
