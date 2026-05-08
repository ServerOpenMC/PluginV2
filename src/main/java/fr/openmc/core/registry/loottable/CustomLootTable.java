package fr.openmc.core.registry.loottable;

import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class CustomLootTable {
    public abstract String getName();
    public abstract Set<CustomLoot> getLoots();

    public double getChanceOf(ItemStack item) {
        return this.getLoots().stream()
                .filter(loot -> ItemUtils.isSimilar(loot.getItem(), item))
                .mapToDouble(CustomLoot::getChance)
                .sum();
    }

    /**
     * Rolls the loot table and returns a list of ItemStacks based on the defined chances.
     * The method calculates the total chance of all loots, generates a random number,
     * and iterates through the loots to determine which one(s) to drop based on their chances.
     * @return A list of ItemStacks representing the rolled loot.
     */
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

    /**
     * Rolls the loot table and returns a list of ItemStacks based on the defined chances, but with a specified amount for each loot.
     * @param amountRoll The amount to set for each rolled loot. This will override the random amount defined in the CustomLoot.
     * @return A list of ItemStacks representing the rolled loot with the specified amount.
     */
    public List<ItemStack> rollLootsWithAmount(int amountRoll) {
        List<ItemStack> loot = new ArrayList<>();

        for (int i = 0; i < amountRoll; i++) {
            loot.addAll(rollLoots());
        }

        return loot;
    }
}