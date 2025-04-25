package fr.openmc.core.features.quests.quests;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineAyweniteQuest extends Quest implements Listener {

    public MineAyweniteQuest() {
        super("Ohhh... c'est précieux ça ?", "Miner {target} Aywenite{s}", CustomItemRegistry.getByName("omc_items:aywenite").getBest());

        this.addTiers(
                new QuestTier(1, new QuestMoneyReward(20)),
                new QuestTier(64, new QuestMoneyReward(140)),
                new QuestTier(512, new QuestItemReward(Material.ANCIENT_DEBRIS, 2))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());
        if (customBlock != null && customBlock.getNamespacedID() != null &&
                ("omc_blocks:aywenite_ore".equals(customBlock.getNamespacedID()) ||
                "omc_blocks:deepslate_aywenite_ore".equals(customBlock.getNamespacedID()))
        ) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }

}
