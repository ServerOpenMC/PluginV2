package fr.openmc.core.registry.loottable;

import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.entity.Player;
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
                .filter(loot -> loot instanceof ItemLoot)
                .map(loot -> (ItemLoot) loot)
                .filter(loot -> loot.getItems().stream()
                        .anyMatch(lootItem -> ItemUtils.isSimilar(lootItem, item)))
                .mapToDouble(CustomLoot::getChance)
                .sum();
    }

    public List<CustomLoot> rollLoots(Player receiver, boolean giveLoots) {
        List<CustomLoot> result = new ArrayList<>();

        double totalChance = this.getLoots().stream()
                .mapToDouble(CustomLoot::getChance)
                .sum();

        double roll = Math.random() * totalChance;
        double sumChance = 0.0;

        for (CustomLoot loot : this.getLoots()) {
            sumChance += loot.getChance();
            if (roll <= sumChance) {
                if (giveLoots)
                    loot.run(receiver);
                result.add(loot);
                break;
            }
        }

        if (result.isEmpty()) {
            CustomLoot next = this.getLoots().iterator().next();
            if (giveLoots)
                next.run(receiver);
            result.add(next);
        }

        return result;
    }

    public List<CustomLoot> rollLoots(Player receiver) {
        return rollLoots(receiver, true);
    }

    public List<CustomLoot> rollLootsWithAmount(Player receiver, int amountRoll) {
        List<CustomLoot> loot = new ArrayList<>();

        for (int i = 0; i < amountRoll; i++) {
            loot.addAll(rollLoots(receiver));
        }

        return loot;
    }

    public CustomLoot selectRandomLoot() {
        double totalChance = this.getLoots().stream()
                .mapToDouble(CustomLoot::getChance)
                .sum();

        double random = ThreadLocalRandom.current().nextDouble(totalChance);
        double cumulative = 0;

        for (CustomLoot loot : this.getLoots()) {
            cumulative += loot.getChance();

            if (random <= cumulative) {
                return loot;
            }
        }

        return this.getLoots().stream().findFirst().orElse(null);
    }

    public List<CustomLoot> generateWeightedPool() {
        List<CustomLoot> pool = new ArrayList<>();
        for (CustomLoot loot : this.getLoots()) {
            int count = Math.max(1, (int) (loot.getChance() * 2));
            for (int i = 0; i < count; i++) {
                pool.add(loot);
            }
        }
        Collections.shuffle(pool);
        return pool;
    }
}