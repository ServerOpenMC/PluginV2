package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;

public class SaveTheEarthQuest extends Quest implements Listener {

    public SaveTheEarthQuest() {
        super(
                TranslationManager.translationString("feature.quests.save_the_earth.name"),
                List.of(TranslationManager.translationString("feature.quests.save_the_earth.description")),
                Material.OAK_SAPLING
        );

        this.addTiers(
                new QuestTier(10, new QuestItemReward(Material.OAK_LOG, 32)),
                new QuestTier(40, new QuestItemReward(Material.OAK_LOG, 64)),
                new QuestTier(100, new QuestItemReward(Material.OAK_LOG, 128)),
                new QuestTier(1000, new QuestItemReward(Material.OAK_LOG, 256))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreak(StructureGrowEvent event) {
        if (event.getPlayer() != null)
            this.incrementProgress(event.getPlayer().getUniqueId());
    }
}
