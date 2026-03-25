package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.api.menulib.events.OpenMenuEvent;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.features.settings.menu.PlayerSettingsMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class OpenSettingsMenuQuest extends MilestoneQuest implements Listener {

    public OpenSettingsMenuQuest() {
        super(
                "Ouvrir le menu des paramètres",
                List.of(
                        "§fTapez §d/settings §fou bien allez dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu",
                        "§8§oCela va vous permettre de configurer votre expérience de jeu !"
                ),
                Material.COMPARATOR,
		        MilestoneType.TUTORIAL,
		        TutorialStep.OPEN_SETTINGS,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.OPEN_SETTINGS.ordinal() + 1) + " §f! Les §9paramètres §fcustomisent votre jeu, ils peuvent être utiles dans certains cas, comme pour bloquer des demandes d'amis, ..." +
								        " Sujet à part, vous pouvez passer en mode compétition grâce aux §6contests§f, une sorte de concours hebdomadaire !",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onSettingsMenuOpen(OpenMenuEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getMenu() == null) return;

        if (!(event.getMenu() instanceof PlayerSettingsMenu)) return;

        this.incrementProgress(player.getUniqueId());
    }

}
