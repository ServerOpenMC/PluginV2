package fr.openmc.core.features.quests.quests;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConsumeKebabQuest extends Quest implements Listener {

    public ConsumeKebabQuest() {
        super(
                TranslationManager.translationString("feature.quests.consume_kebab.name"),
                List.of(TranslationManager.translationString("feature.quests.consume_kebab.description")),
                OMCRegistry.CUSTOM_ITEMS.KEBAB
        );

        this.addTiers(
                new QuestTier(10, new QuestMoneyReward(30)),
                new QuestTier(64, new QuestMoneyReward(80)),
                new QuestTier(256, new QuestMoneyReward(160)),
                new QuestTier(1024, new QuestMoneyReward(400))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.isSimilar(OMCRegistry.CUSTOM_ITEMS.KEBAB.getBest())) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }

}
