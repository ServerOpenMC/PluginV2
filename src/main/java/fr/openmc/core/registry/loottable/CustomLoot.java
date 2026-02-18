package fr.openmc.core.registry.loottable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class CustomLoot {

    private final ItemStack item;
    private final double chance;
    private final int minAmount;
    private final int maxAmount;

    public CustomLoot(ItemStack item, double chance, int minAmount, int maxAmount) {
        this.item = item;
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public CustomLoot(CustomItem item, double chance, int minAmount, int maxAmount) {
        if (item == null) {
            throw new IllegalArgumentException("CustomItem cannot be null");
        }
        this.item = item.getBest();
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public int getRandomAmount() {
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }
}