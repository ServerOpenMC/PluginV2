package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.api.menulib.events.OpenMenuEvent;
import fr.openmc.core.features.adminshop.menus.AdminShopMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class OpenAdminShopMenuQuest extends MilestoneQuest implements Listener {

    public OpenAdminShopMenuQuest() {
        super(
                TranslationManager.translationString("feature.milestones.tutorial.quest.open_adminshop.name"),
                List.of(
                        TranslationManager.translationString("feature.milestones.tutorial.quest.open_adminshop.description.1"),
                        TranslationManager.translationString("feature.milestones.tutorial.quest.open_adminshop.description.2")
                ),
                Material.EMERALD,
                MilestoneType.TUTORIAL,
                TutorialSteps.OPEN_ADMINSHOP,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.open_adminshop.reward",
                                        Component.text(TutorialSteps.OPEN_ADMINSHOP.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
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
