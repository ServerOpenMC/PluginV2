package fr.openmc.core.registry.loottable;

import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class CustomLootTable {
    public abstract String getNamespace();
    public abstract Set<CustomLoot> getLoots();

    public double getChanceOf(ItemStack item) {
        return this.getLoots().stream()
                .filter(loot -> loot.items().stream()
                        .anyMatch(lootItem -> ItemUtils.isSimilar(lootItem, item)))
                .mapToDouble(CustomLoot::chance)
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
                .mapToDouble(CustomLoot::chance)
                .sum();

        double roll = Math.random() * totalChance;
        double sumChance = 0.0;

        for (CustomLoot loot : this.getLoots()) {
            sumChance += loot.chance();
            if (roll <= sumChance) {
                for (ItemStack lootItem : loot.items()) {
                    ItemStack item = lootItem.clone();
                    item.setAmount(loot.getRandomAmount());
                    result.add(item);
                }
                break;
            }
        }

        if (result.isEmpty()) {
            CustomLoot next = this.getLoots().iterator().next();
            for (ItemStack lootItem : next.items()) {
                ItemStack item = lootItem.clone();
                item.setAmount(next.getRandomAmount());
                result.add(item);
            }
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

    public CustomLoot selectRandomLoot() {
        double totalChance = this.getLoots().stream()
                .mapToDouble(CustomLoot::chance)
                .sum();

        double random = ThreadLocalRandom.current().nextDouble(totalChance);
        double cumulative = 0;

        for (CustomLoot item : this.getLoots()) {
            cumulative += item.chance();

            if (random <= cumulative) {
                return item;
            }
        }

        return this.getLoots().stream().findFirst().orElse(null);
    }

    public List<CustomLoot> generateWeightedPool() {
        List<CustomLoot> pool = new ArrayList<>();
        for (CustomLoot item : this.getLoots()) {
            int count = Math.max(1, (int) (item.chance() * 2));
            for (int i = 0; i < count; i++) {
                pool.add(item);
            }
        }
        Collections.shuffle(pool);
        return pool;
    }
}