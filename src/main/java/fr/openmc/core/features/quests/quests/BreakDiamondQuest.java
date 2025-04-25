package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakDiamondQuest extends Quest implements Listener {

    public BreakDiamondQuest() {
        super(
                "Richou",
                "Casser {target} minerai{s} de diamant",
                Material.DIAMOND
        );

        this.addTiers(
                new QuestTier(100, new QuestMoneyReward(2500)),
                new QuestTier(400, new QuestMoneyReward(5000)),
                new QuestTier(800, new QuestMoneyReward(10000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.DIAMOND_ORE)
                || event.getBlock().getType().equals(Material.DEEPSLATE_DIAMOND_ORE)
        ) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }
}
