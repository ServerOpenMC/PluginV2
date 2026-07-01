package fr.openmc.core.features.quests.quests;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
        super(
                TranslationManager.translationString("feature.quests.mine_aywenite.name"),
                List.of(TranslationManager.translationString("feature.quests.mine_aywenite.description")),
                OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest()
        );

        this.addTiers(
                new QuestTier(1, new QuestMoneyReward(20)),
                new QuestTier(64, new QuestMoneyReward(140)),
                new QuestTier(512, new QuestItemReward(Material.DIAMOND, 6))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH))
            return; // Ne pas compter si le joueur utilise Silk Touch

        if (!ItemsAdderHook.isEnable())
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