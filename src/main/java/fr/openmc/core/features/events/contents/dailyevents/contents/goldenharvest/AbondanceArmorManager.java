package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AbondanceArmorManager {
    public static final double DOUBLE_CROPS_MODIFIER = 0;

    public static final double DOUBLE_CROPS_ARMOR_MODIFIER = 0.05;
    public static final double LUCK_GOLDEN_CROPS_ARMOR_MODIFIER = 0.015;


    private static final Set<CustomItem> ABONDANCE_ARMOR = Set.of(
            OMCRegistry.CUSTOM_ITEMS.ABONDANCE_HELMET,
            OMCRegistry.CUSTOM_ITEMS.ABONDANCE_CHESTPLATE,
            OMCRegistry.CUSTOM_ITEMS.ABONDANCE_LEGGINGS,
            OMCRegistry.CUSTOM_ITEMS.ABONDANCE_BOOTS
    );

    public static double getDoubleCropsModifier(Player player) {
        double base = DOUBLE_CROPS_MODIFIER;

        PlayerInventory inv = player.getInventory();

        ItemStack[] armor = {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack item : armor) {
            Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(item);

            if (ci.isPresent() && ABONDANCE_ARMOR.contains(ci.get())) {
                base += DOUBLE_CROPS_ARMOR_MODIFIER;
            }
        }

        return base;
    }

    public static Collection<CustomLoot> applyDoubleCropsChance(Player player, Collection<CustomLoot> loots) {
        double doubleHookChance = getDoubleCropsModifier(player);
        if (doubleHookChance == 0) return loots;
        if (ThreadLocalRandom.current().nextDouble() >= doubleHookChance) return loots;

        List<CustomLoot> doubledLoots = new ArrayList<>(loots);
        doubledLoots.addAll(loots);

        return doubledLoots;
    }

    /**
     * Donne la chance de pouvoir avoir une double prise
     * @param player le joueur ciblé
     * @return la chance associé au joueur
     */
    public static double getLuckGoldenCropsModifier(Player player) {
        double base = GoldenHarvestManager.GOLDEN_CROP_ON_CROP_CHANCE;

        PlayerInventory inv = player.getInventory();

        ItemStack[] armor = {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack item : armor) {
            Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(item);

            if (ci.isPresent() && ABONDANCE_ARMOR.contains(ci.get())) {
                base += LUCK_GOLDEN_CROPS_ARMOR_MODIFIER;
            }
        }

        return base;
    }
}
