package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.api.menulib.events.OpenMenuEvent;
import fr.openmc.core.features.adminshop.menus.AdminShopMenu;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
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

public class OpenAdminShopMenuQuest extends MilestoneQuest implements Listener {

    public OpenAdminShopMenuQuest() {
        super(
                "Ouvrir le menu de l'adminshop",
                List.of(
                        "§fTapez §c/adminshop §fou bien allez dans le §dmenu principal (/menu) §fpour pouvoir ouvrir le menu",
                        "§8§oLe marché qui varie en fonction de l'offre et de la demande !"
                ),
                Material.EMERALD,
		        MilestoneType.TUTORIAL,
		        TutorialStep.OPEN_ADMINSHOP,
		        new QuestTier(
				        1,
				        new QuestMoneyReward(500),
				        new QuestTextReward(
						        "Bien joué ! Vous avez fini l'§6étape " + (TutorialStep.OPEN_ADMINSHOP.ordinal() + 1) + " §f! L'§cadminshop §fvous servira à vous procurer de l'argent et des blocs ! Vous pouvez d'ailleurs dès maintenant vendre ou acheter une ressource à l'adminshop !",
						        Prefix.MILLESTONE,
						        MessageType.SUCCESS
				        )
		        )
        );
    }

    @EventHandler
    public void onAdminShopMenuOpen(OpenMenuEvent event) {
        Player player = event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getMenu() == null) return;

        if (!(event.getMenu() instanceof AdminShopMenu)) return;

        this.incrementProgress(player.getUniqueId());
    }

}
