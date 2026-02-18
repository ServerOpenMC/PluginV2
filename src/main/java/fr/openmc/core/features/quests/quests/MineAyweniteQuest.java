package fr.openmc.core.features.quests.quests;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.registry.items.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MineAyweniteQuest extends Quest implements Listener {

    public MineAyweniteQuest() {
        super("Ohhh... c'est précieux ça ?", List.of("Miner {target} Aywenite{s}"), CustomItemRegistry.getByName("omc_items:aywenite").getBest());

        this.addTiers(
                new QuestTier(1, new QuestMoneyReward(20)),
                new QuestTier(64, new QuestMoneyReward(140)),
                new QuestTier(512, new QuestItemReward(Material.DIAMOND, 6))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return; // Ne pas compter si le joueur utilise Silk Touch
        }

        if (!ItemsAdderHook.isHasItemAdder())
            return;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());
        if (customBlock != null && customBlock.getNamespacedID() != null &&
                ("omc_blocks:aywenite_ore".equals(customBlock.getNamespacedID()) ||
                        "omc_blocks:deepslate_aywenite_ore".equals(customBlock.getNamespacedID()))
        ) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }
}