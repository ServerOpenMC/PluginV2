package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.features.settings.menu.PlayerSettingsMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class OpenSettingsMenuQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public OpenSettingsMenuQuest() {
        super(
                "Ouvrez le menu des Paramètres",
                List.of(
                        "§fTapez §d/settings §fou bien aller dans le §dmenu principal /menu §fpour pouvoir ouvrir le menu",
                        "§8§oCela va vous permettre de configurer votre expérience de jeu !"
                ),
                Material.COMPARATOR
        );

        this.step = TutorialStep.OPEN_SETTINGS;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué! Vous avez fini l'§6Etape " + (step.ordinal() + 1) + " §f! Les §9Paramètres §fcustomisent votre jeu, ils peuvent être utile dans certains cas. Sujet à part, accàder aux Menus des §6Contests§f, une sorte de concours tout les week end !",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onSettingsMenuOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getInventory().getHolder() == null) return;

        if (!event.getInventory().getHolder().getClass().equals(PlayerSettingsMenu.class)) return;

        this.incrementProgress(player.getUniqueId());
    }

}
