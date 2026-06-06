package fr.openmc.core.registry.loottable;

import fr.openmc.core.registry.items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public record CustomLoot(Set<ItemStack> items, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {

    public CustomLoot(ItemStack item, double chance, int minAmount, int maxAmount) {
        this(Collections.singleton(item),
                null,
                chance,
                minAmount,
                maxAmount);
    }

    public CustomLoot(CustomItem item, double chance, int minAmount, int maxAmount) {
        if (item == null) {
            throw new IllegalArgumentException("CustomItem cannot be null");
        }
        this(Collections.singleton(item.getBest()),
                null,
                chance,
                minAmount,
                maxAmount);
    }

    public CustomLoot(ItemStack item, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {
        this(Collections.singleton(item),
                displayedItem,
                chance,
                minAmount,
                maxAmount);
    }

    public CustomLoot(CustomItem item, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {
        if (item == null) {
            throw new IllegalArgumentException("CustomItem cannot be null");
        }
        this(Collections.singleton(item.getBest()),
                displayedItem,
                chance,
                minAmount,
                maxAmount);
    }

    public ItemStack getFirstLoot() {
        if (items.size() == 1) {
            return items.iterator().next();
        }
        return items.stream().findFirst().orElse(null);
    }

    public int getRandomAmount() {
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }
}