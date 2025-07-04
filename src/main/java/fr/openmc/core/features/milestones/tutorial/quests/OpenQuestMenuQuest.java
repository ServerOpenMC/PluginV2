package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.tutorial.TutorialStep;
import fr.openmc.core.features.milestones.tutorial.utils.TutorialUtils;
import fr.openmc.core.features.quests.menus.QuestsMenu;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class OpenQuestMenuQuest extends Quest implements Listener {

    private final TutorialStep step;
    private final MilestoneType type;

    public OpenQuestMenuQuest() {
        super(
                "Ouvrez le menu des Quêtes",
                List.of(
                        "§fTapez §d/quests §fou bien aller dans le §dmenu principal /menu §fpour pouvoir ouvrir le menu",
                        "§8§oCela va pouvoir vous lancer dans l'aventure et de vous diversifier !"
                ),
                Material.GOLDEN_AXE
        );

        this.step = TutorialStep.OPEN_QUEST;
        this.type = MilestoneType.TUTORIAL;

        this.addTier(new QuestTier(
                1,
                new QuestMoneyReward(500),
                new QuestTextReward(
                        "Bien Joué! Vous avez fini l'§6Etape " + (step.ordinal() + 1) + " §f! Les §9Quêtes §fvous serviront à vous procurer de l'argent facilement pour le §9début de jeu §f! D'ailleurs, accomplissez une des §9tâches §fque vous voulez !",
                        Prefix.MILLESTONE,
                        MessageType.SUCCESS
                ),
                new QuestMethodsReward(
                        player -> TutorialUtils.completeStep(type, player, step)
                )
        ));
    }

    @EventHandler
    public void onQuestMenuOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (MilestonesManager.getPlayerStep(type, player) != step.ordinal()) return;

        if (event.getInventory().getHolder() == null) return;

        if (!event.getInventory().getHolder().getClass().equals(QuestsMenu.class)) return;

        this.incrementProgress(player.getUniqueId());
    }

}
