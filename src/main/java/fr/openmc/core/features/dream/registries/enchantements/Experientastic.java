package fr.openmc.core.features.dream.registries.enchantements;

import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.features.dream.registries.DreamEnchantementRegistry;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

@SuppressWarnings("UnstableApiUsage")
public class Experientastic extends DreamEnchantment implements Listener {

    @Override
    public Key getKey() {
        return Key.key("dream:experientastic");
    }

    @Override
    public Component getName() {
        return Component.text("Experientastic");
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
        return 4;
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
    public void onAttack(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        Enchantment enchant = DreamEnchantementRegistry.getEnchantment(getKey());
        if (enchant == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getEnchantments().containsKey(enchant)) return;

        int level = item.getEnchantments().getOrDefault(enchant, 0);
        if (level <= 0) return;

        double multiplier;
        switch (level) {
            case 1 -> multiplier = 1.5;
            case 2 -> multiplier = 2.0;
            case 3 -> multiplier = 3.0;
            default -> multiplier = 1.0;
        }

        int newExp = (int) Math.round(event.getDroppedExp() * multiplier);
        event.setDroppedExp(newExp);
    }
}
