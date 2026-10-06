package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame.FishingMiniGameResult;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class FishingAttributeManager {
    public static final double FISHING_SPEED_MODIFIER = 0.4;
    public static final double DOUBLE_HOOK_MODIFIER = 0;

    public static final double ARMOR_FISHING_SPEED_MODIFIER = 0.05;
    public static final double ARMOR_DOUBLE_HOOK_MODIFIER = 0.03;

    private static final Set<CustomItem> FISHER_ARMOR = Set.of(
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_HELMET,
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_CHESTPLATE,
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_LEGGINGS,
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_BOOTS
    );

    public static void applyFishingSpeedModifier(Player player, FishHook hook) {
        double fishingSpeed = getFishingSpped(player);
        hook.setWaitTime((int) (hook.getMinWaitTime() * (1 - fishingSpeed)),
                (int) (hook.getMaxWaitTime() * (1 - fishingSpeed)));
    }

    public static double getFishingSpped(Player player) {
        double base = FISHING_SPEED_MODIFIER;
        PlayerInventory inv = player.getInventory();

        ItemStack[] armor = {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack item : armor) {
            Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(item);
            if (ci.isPresent() && FISHER_ARMOR.contains(ci.get())) {
                base += ARMOR_FISHING_SPEED_MODIFIER;
            }
        }

        return base;
    }

    public static double getDoubleHookChance(Player player) {
        double base = DOUBLE_HOOK_MODIFIER;
        PlayerInventory inv = player.getInventory();

        ItemStack[] armor = {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack item : armor) {
            Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(item);
            if (ci.isPresent() && FISHER_ARMOR.contains(ci.get())) {
                base += ARMOR_DOUBLE_HOOK_MODIFIER;
            }
        }

        return base;
    }

    /**
     * Génère le loot en fonction du résultat du mini-jeu.
     * Un meilleur résultat augmente le poids relatif des loots les plus rares.
     */
    public static List<CustomLoot> rollFishingLoots(FishingMiniGameResult miniGameResult) {
        CustomLootTable fishingLootTable = OMCRegistry.CUSTOM_LOOT_TABLES.MIRACULOUS_FISHING;

        List<CustomLoot> allLoots = new ArrayList<>(fishingLootTable.getLoots());

        List<CustomLoot> resultLoots = allLoots.stream()
                .filter(loot -> loot.getChance() >= 1.0)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        List<CustomLoot> candidates = allLoots.stream()
                .filter(loot -> loot.getChance() < 1.0)
                .toList();

        if (candidates.isEmpty()) {
            return resultLoots;
        }

        double maxChance = candidates.stream()
                .mapToDouble(CustomLoot::getChance)
                .max()
                .orElse(0.0);

        if (maxChance <= 0.0) {
            resultLoots.add(candidates.getFirst());
            return resultLoots;
        }

        double totalWeight = candidates.stream()
                .mapToDouble(loot -> getRarityAdjustedChance(loot.getChance(), maxChance, miniGameResult))
                .sum();

        if (totalWeight <= 0.0) {
            resultLoots.add(candidates.getFirst());
            return resultLoots;
        }

        double roll = ThreadLocalRandom.current().nextDouble(totalWeight);
        double cumulativeWeight = 0.0;

        for (CustomLoot loot : candidates) {
            cumulativeWeight += getRarityAdjustedChance(loot.getChance(), maxChance, miniGameResult);
            if (roll < cumulativeWeight) {
                resultLoots.add(loot);
                return resultLoots;
            }
        }

        resultLoots.add(candidates.getLast());
        return resultLoots;
    }

    /**
     * Ajuste le poids d'un loot selon sa rareté.
     * Plus la chance de base est faible, plus le bonus est important.
     */
    static double getRarityAdjustedChance(double chance, double maxChance, FishingMiniGameResult miniGameResult) {
        if (chance <= 0.0 || maxChance <= 0.0) {
            return 0.0;
        }

        double rarity = Math.max(0.0, Math.min(1.0, 1.0 - (chance / maxChance)));
        return chance * (1.0 + miniGameResult.getRarityBonus() * rarity);
    }

    public static List<CustomLoot> applyDoubleHookChance(Player player, List<CustomLoot> loots) {
        double doubleHookChance = getDoubleHookChance(player);
        if (doubleHookChance == 0) return loots;
        if (ThreadLocalRandom.current().nextDouble() >= doubleHookChance) return loots;

        List<CustomLoot> doubledLoots = new ArrayList<>(loots);
        doubledLoots.addAll(loots);
        return doubledLoots;
    }
}
