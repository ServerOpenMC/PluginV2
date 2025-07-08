package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class SmeltIronQuest extends Quest implements Listener {

    public SmeltIronQuest() {
        super("Chaud devant !", "Faire fondre {target} lingots de fer", Material.IRON_ORE);

        this.addTiers(
                new QuestTier(256, new QuestMoneyReward(2000)),
                new QuestTier(512, new QuestMoneyReward(4000)),
                new QuestTier(1536, new QuestMoneyReward(8000))
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
