package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import java.util.List;

public class SmeltIronQuest extends Quest implements Listener {

    public SmeltIronQuest() {
        super(
                TranslationManager.translationString("feature.quests.smelt_iron.name"),
                List.of(TranslationManager.translationString("feature.quests.smelt_iron.description")),
                Material.IRON_ORE
        );

        this.addTiers(
                new QuestTier(256, new QuestMoneyReward(500)),
                new QuestTier(512, new QuestMoneyReward(1200)),
                new QuestTier(1536, new QuestMoneyReward(2500))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSmelt(FurnaceExtractEvent event) {
        if (event.getItemType().equals(Material.IRON_INGOT)) {
            int amount = event.getItemAmount();
            this.incrementProgress(event.getPlayer().getUniqueId(), amount);
        }
    }
}
