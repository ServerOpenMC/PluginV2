package fr.openmc.core.features.dream.registries.enchantements;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.features.dream.registries.DreamEnchantementRegistry;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("UnstableApiUsage")
public class DreamSleeper extends DreamEnchantment implements Listener {

    @Override
    public Key getKey() {
        return Key.key("dream:dream_sleeper");
    }

    @Override
    public Component getName() {
        return Component.text("Endormant");
    }

    @Override
    public TagKey<ItemType> getSupportedItems() {
        return ItemTypeTagKeys.SWORDS;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @Override
    public int getAnvilCost() {
        return 3;
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 1);
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(3, 4);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        Enchantment enchant = DreamEnchantementRegistry.getEnchantment(getKey());
        if (enchant == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getEnchantments().containsKey(enchant)) return;

        if (player.hasCooldown(item)) return;
        player.setCooldown(item, 5 * 20);

        living.setAI(false);
        int level = item.getEnchantments().get(enchant);

        new BukkitRunnable() {
            @Override
            public void run() {
                living.setAI(true);
            }
        }.runTaskLater(OMCPlugin.getInstance(), 15L * level);
    }
}
