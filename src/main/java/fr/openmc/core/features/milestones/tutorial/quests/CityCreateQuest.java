package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.events.MemberJoinEvent;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
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

    private final TutorialStep step;
    private final MilestoneType type;

    public CityCreateQuest() {
        super(
                "Créer/Rejoindre une ville",
                List.of(
                        "§fFaites §d/city §fpour commencer à créer votre ville",
                        "§fou bien rejoindre une ville à l'aide d'une invitation !"
                ),
                Material.OAK_DOOR
        );

        this.step = TutorialStep.CITY_CREATE;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué ! Vous avez fini l'§6Étape " + (step.ordinal() + 1) + " §f! Cette version d'OpenMC est centrée autour des villes. Vous y trouverez une §eMilestone spéciale pour les Villes §f (/city milestone) qui vous guidera dans cette aventure singulière ! Et si vous passiez votre ville au §3Niveau 2 §f?",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> {
                            TutorialUtils.completeStep(type, player, step);

                            City playerCity = CityManager.getPlayerCity(player.getUniqueId());
                            if (playerCity.getLevel() >= 2) {
                                TutorialStep.CITY_LEVEL_2.getQuest().incrementProgress(player.getUniqueId());
                            }
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

        if (player.isOnline()) {
            this.incrementProgress(player.getUniqueId());
        }
    }
}
