package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.contents.enchantments;


import fr.openmc.core.OMCPlugin;
import fr.openmc.core.registry.enchantments.CustomEnchantment;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

@SuppressWarnings("UnstableApiUsage")
public class Plantation extends CustomEnchantment implements Listener {
    // Bonus d'âge par niveau, appliqué à partir du niveau II (10% par niveau)
    private static final double AGE_BONUS_PER_LEVEL = 0.10;

    @Override
    public Key getKey() {
        return Key.key("omc_daily_events:plantation");
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.golden_harvest.enchantment.plantation.name");
    }

    @Override
    public TagKey<ItemType> getSupportedItems() {
        return ItemTypeTagKeys.HOES;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @Override
    public int getAnvilCost() {
        return 6;
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 3);
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(4, 3);
    }

    @EventHandler
    public void onBreakCrops(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        BlockData blockData = block.getBlockData();

        if (!(blockData instanceof Ageable)) return;

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        Enchantment enchantment = getEnchantment();
        int level = tool.getEnchantmentLevel(enchantment);
        if (level <= 0) return;

        BlockData replantData = blockData.clone();
        Ageable ageable = (Ageable) replantData;
        int maxAge = ageable.getMaximumAge();

        // * Mécanique de repousse des cultures avec bonus d'âge selon le niveau de l'enchantement
        int startAge = 0;
        if (level >= 2) {
            double bonusFraction = (level - 1) * AGE_BONUS_PER_LEVEL;
            startAge = (int) Math.round(maxAge * bonusFraction);
            startAge = Math.min(startAge, maxAge);
        }
        ageable.setAge(startAge);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            block.setType(blockData.getMaterial());
            block.setBlockData(replantData, false);
        });
    }
}
