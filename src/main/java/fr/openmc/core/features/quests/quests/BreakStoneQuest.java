package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakStoneQuest extends Quest implements Listener {

    public BreakStoneQuest() {
        super("Casseur de pierres","Miner {target} blocs de pierre", new ItemStack(Material.DIAMOND_PICKAXE));


        this.addTiers(
                new QuestTier(10000, new QuestMoneyReward(2000)),
                new QuestTier(30000, new QuestMoneyReward(4000)),
                new QuestTier(80000, new QuestMoneyReward(6000)),
                new QuestTier(150000, new QuestMoneyReward(10000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.STONE)) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }

}
