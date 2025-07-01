package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.events.MemberJoinEvent;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class CityCreateQuest extends Quest implements Listener {

    private final TutorialStep step = TutorialStep.CITY_CREATE;
    private final MilestoneType type = MilestoneType.TUTORIAL;

    public CityCreateQuest() {
        super(
                "Créer/Rejoindre une ville",
                List.of(
                        "Faite §d/city §fpour commencer à créer votre ville",
                        "ou bien rejoindre une ville en ayant une invitation!"
                ),
                Material.OAK_DOOR
        );

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué! Vous avez fini l'§6Etape 2 §f! Cette version est centrée autour des villes. Vous y trouverez un §eMilestone spécial pour les Villes §fqui vous guideront dans cette aventure pas comme les autres ! Mais avant cela, faudrait peut être mettre un §2Home§f?",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        (player) -> {
                            MilestonesManager.setPlayerStep(type, player, step.ordinal());
                        }
                )
        ));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCityCreate(CityCreationEvent event) {
        Player player = event.getOwner();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        this.incrementProgress(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinCity(MemberJoinEvent event) {
        OfflinePlayer player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player.getUniqueId()) != step.ordinal()) return;

        if (player.isOnline()) this.incrementProgress(player.getUniqueId());
    }
}
