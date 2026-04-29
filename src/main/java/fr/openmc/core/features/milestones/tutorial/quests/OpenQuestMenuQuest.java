package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.api.menulib.events.OpenMenuEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.menus.QuestsMenu;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class OpenQuestMenuQuest extends MilestoneQuest implements Listener {

    public OpenQuestMenuQuest() {
        super(
                "Ouvrir le menu des quêtes",
                List.of(
                        "§fTapez §d/quests §fou bien allez dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu et voir quelles quêtes vous pouvez accomplir",
                        "§8§oCela va pouvoir vous lancer dans l'aventure et vous donner des défis afin de vous diversifier !"
                ),
                Material.GOLDEN_AXE,
		        MilestoneType.TUTORIAL,
		        TutorialStep.OPEN_QUEST,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.OPEN_QUEST.ordinal() + 1) + " §f! Les §9quêtes §fvous serviront à vous procurer de l'argent facilement pour le §9début de jeu §f! Vous pouvez tenter d'accomplir la §9tâche §fque vous voulez !",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onQuestMenuOpen(OpenMenuEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getMenu() == null) return;

        if (!(event.getMenu() instanceof QuestsMenu)) return;

        this.incrementProgress(player.getUniqueId());
    }

}
