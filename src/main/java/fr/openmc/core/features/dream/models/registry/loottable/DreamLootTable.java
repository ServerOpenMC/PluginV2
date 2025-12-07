package fr.openmc.core.features.dream.models.registry.loottable;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class DreamLootTable {
    public abstract Key getKey();

    public abstract Set<DreamLoot> getLoots();

    public List<ItemStack> rollLoots() {
        List<ItemStack> result = new ArrayList<>();

        double totalChance = this.getLoots().stream()
                .mapToDouble(DreamLoot::chance)
                .sum();

        double roll = Math.random() * totalChance;
        double sumChance = 0.0;

        for (DreamLoot loot : this.getLoots()) {
            sumChance += loot.chance();
            if (roll <= sumChance) {
                ItemStack item = loot.item().getBest();
                item.setAmount(loot.getRandomAmount());
                result.add(item);
                break;
            }
        }

        if (result.isEmpty()) {
            DreamLoot next = this.getLoots().iterator().next();
            ItemStack item = next.item().getBest();
            item.setAmount(next.getRandomAmount());
            result.add(item);
        }

        return result;
    }
}
