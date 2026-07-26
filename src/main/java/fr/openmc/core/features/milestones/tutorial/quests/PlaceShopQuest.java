package fr.openmc.core.features.milestones.tutorial.quests;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.milestones.tutorial.TutorialSteps;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.features.shops.events.PlaceShopEvent;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlaceShopQuest extends MilestoneQuest implements Listener {

    public PlaceShopQuest() {
        super(
                TranslationManager.translation("feature.milestones.tutorial.quest.place_shop.name"),
                TranslationManager.translationLore("feature.milestones.tutorial.quest.place_shop.description"),
                OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_SHOP,
                MilestoneType.TUTORIAL,
                TutorialSteps.PLACE_SHOP,
                new QuestTier(
                        1,
                        new QuestMoneyReward(500),
                        new QuestTextReward(
                                TranslationManager.translation(
                                        "feature.milestones.tutorial.quest.place_shop.reward",
                                        Component.text(TutorialSteps.PLACE_SHOP.ordinal() + 1).color(NamedTextColor.GOLD)
                                ),
                                Prefix.MILLESTONE,
                                MessageType.SUCCESS
                        )
                )
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlaceShop(PlaceShopEvent event) {
        if (MilestonesManager.getPlayerStep(type, event.getPlayer()) != step.ordinal()) return;

        Player player = event.getPlayer();
        this.incrementProgress(player.getUniqueId());
    }
}