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
        hook.setWaitTime((int) (hook.getMinWaitTime() * (1 - fishingSpeed)),
                (int) (hook.getMaxWaitTime() * (1 - fishingSpeed)));
    }

    /**
     * Donne la fishing speed d'un joueur en prenant compte des modifieurs de la peche miraculeuse
     * @param player le joueur ciblé
     * @return le pourcentage d'augmentation de vitesse de pêche
     */
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
                base += 0.05;
            }
        }

        return base;
    }

    /**
     * Donne la chance de pouvoir avoir une double prise
     * @param player le joueur ciblé
     * @return la chance associé au joueur
     */
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
                base += 0.03;
            }
        }

        return base;
    }

    /**
     * Applique le modifier de double prise
     * @param player le joueur ciblé
     * @param loots les loots initiaux
     * @return les loots finaux
     */
    public static List<CustomLoot> applyDoubleHookChance(Player player, List<CustomLoot> loots) {
        double doubleHookChance = getDoubleHookChance(player);
        if (doubleHookChance == 0) return loots;
        if (ThreadLocalRandom.current().nextDouble() >= doubleHookChance) return loots;

        List<CustomLoot> doubledLoots = new ArrayList<>(loots);
        doubledLoots.addAll(loots);

        return doubledLoots;
    }
}
