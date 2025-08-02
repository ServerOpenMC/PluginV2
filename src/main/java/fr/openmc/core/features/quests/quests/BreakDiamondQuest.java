package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BreakDiamondQuest extends Quest implements Listener {

    public BreakDiamondQuest() {
        super(
                "Richou",
                List.of("Casser {target} minerai{s} de diamant"),
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
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return; // Ne pas compter si le joueur utilise Silk Touch
        }

        Material type = event.getBlock().getType();
        if (type.equals(Material.DIAMOND_ORE)
                || type.equals(Material.DEEPSLATE_DIAMOND_ORE)
        ) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }
}
