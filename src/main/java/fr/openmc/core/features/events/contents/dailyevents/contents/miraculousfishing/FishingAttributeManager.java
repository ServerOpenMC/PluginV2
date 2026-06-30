package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
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

    private static final Set<CustomItem> FISHER_ARMOR = Set.of(
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_HELMET,
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_CHESTPLATE,
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_LEGGINGS,
            OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_BOOTS
    );

    /**
     * Applique un modificateur de vitesse de pêche à un FishHook.
     * @param hook le hook qui aura son temps réduit
     */
    public static void applyFishingSpeedModifier(Player player, FishHook hook) {
        double fishingSpeed = getFishingSpped(player);
        hook.setWaitTime((int) fishingSpeed,
                (int) fishingSpeed);
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

            if (ci.isPresent() && FISHER_ARMOR.stream()
                    .anyMatch(customItem -> customItem.getId().equals(ci.get().getId()))) {
                base += 0.015;
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

            if (ci.isPresent() && FISHER_ARMOR.stream()
                    .anyMatch(customItem -> customItem.getId().equals(ci.get().getId()))) {
                base += 0.025;
            }
        }

        return base;
    }

    public static List<CustomLoot> applyDoubleHookChance(Player player, List<CustomLoot> loots) {
        if (ThreadLocalRandom.current().nextDouble() >= getDoubleHookChance(player)) return loots;

        List<CustomLoot> doubledLoots = new ArrayList<>(loots);
        doubledLoots.addAll(loots);

        return doubledLoots;
    }
}
