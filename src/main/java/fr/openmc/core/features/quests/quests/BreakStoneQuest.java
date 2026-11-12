package fr.openmc.core.features.quests.quests;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.contents.Hammer;
import fr.openmc.core.registry.items.contents.UseHammerEvent;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

public class BreakStoneQuest extends Quest implements Listener {
    private static final Set<Material> STONE_TYPES = Set.of(
            Material.STONE,
            Material.DEEPSLATE
    );

    public BreakStoneQuest() {
        super(
                TranslationManager.translation("feature.quests.break_stone.name"),
                TranslationManager.translationLore("feature.quests.break_stone.description"),
                new ItemStack(Material.DIAMOND_PICKAXE)
        );

        this.addTiers(
                new QuestTier(10000, new QuestMoneyReward(500)),
                new QuestTier(30000, new QuestMoneyReward(2000)),
                new QuestTier(80000, new QuestMoneyReward(3500)),
                new QuestTier(150000, new QuestMoneyReward(5500))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        Optional<CustomItem> custom = OMCRegistry.CUSTOM_ITEMS.get(tool);
        if (custom.isPresent() && custom.get() instanceof Hammer)
            return;

        Material material = event.getBlock().getType();
        if (!STONE_TYPES.contains(material))
            return;

        this.incrementProgress(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerUseHammer(UseHammerEvent event) {
        Material material = event.getBlock().getType();
        if (!STONE_TYPES.contains(material))
            return;

        this.incrementProgress(event.getPlayer().getUniqueId(), event.getCountBlockBroken());
    }
}
