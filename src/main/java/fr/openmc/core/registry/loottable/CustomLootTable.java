package fr.openmc.core.registry.loottable;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class CustomLootTable {
    public abstract String getName();
    public abstract Set<CustomLoot> getLoots();

    public List<ItemStack> rollLoots() {
        List<ItemStack> result = new ArrayList<>();

        double totalChance = this.getLoots().stream()
                .mapToDouble(CustomLoot::getChance)
                .sum();

        double roll = Math.random() * totalChance;
        double sumChance = 0.0;

        for (CustomLoot loot : this.getLoots()) {
            sumChance += loot.getChance();
            if (roll <= sumChance) {
                ItemStack item = loot.getItem();
                item.setAmount(loot.getRandomAmount());
                result.add(item);
                break;
            }
        }

        if (result.isEmpty()) {
            CustomLoot next = this.getLoots().iterator().next();
            ItemStack item = next.getItem();
            item.setAmount(next.getRandomAmount());
            result.add(item);
        }

        return result;
    }
}