package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakStoneQuest extends Quest implements Listener {

    public BreakStoneQuest() {
        super("Mineur", "Miner des blocs pour obtenir de l'argent", new ItemStack(Material.DIAMOND_PICKAXE));


        this.addTier(
                new QuestTier(
                        10,
                        new QuestItemReward(Material.DIAMOND, 1),
                        "Miner {number} blocs de pierre"
                )
        );
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.STONE) {
            this.incrementProgress(event.getPlayer().getUniqueId(), 1);
        }
    }

}
