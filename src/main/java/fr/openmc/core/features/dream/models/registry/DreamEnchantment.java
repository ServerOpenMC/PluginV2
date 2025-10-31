package fr.openmc.core.features.dream.models.registry;

import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemType;

@SuppressWarnings("UnstableApiUsage")
public abstract class DreamEnchantment {
    public abstract Key getKey();

    public abstract Component getName();

    public abstract TagKey<ItemType> getSupportedItems();

    public abstract int getMaxLevel();

    public abstract int getWeight();

    public abstract int getAnvilCost();

    public abstract EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    public abstract EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost();
}
