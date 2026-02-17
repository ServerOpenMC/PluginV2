package fr.openmc.core.registry.enchantments;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.enchantements.DreamSleeper;
import fr.openmc.core.features.dream.registries.enchantements.Experientastic;
import fr.openmc.core.features.dream.registries.enchantements.Soulbound;
import fr.openmc.core.registry.items.CustomItemRegistry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class CustomEnchantmentRegistry {
    private final static HashMap<Key, CustomEnchantment> customEnchantments = new HashMap<>();

    public static void init() {
        // ** REGISTER ENCHANTMENTS **
        register(
                new Soulbound(),
                new Experientastic(),
                new DreamSleeper()
        );
    }

    public static void register(CustomEnchantment enchantment) {
        customEnchantments.put(enchantment.getKey(), enchantment);
    }
    public static void register(CustomEnchantment... enchantments) {
        for (CustomEnchantment enchantment : enchantments) {
            register(enchantment);
        }
    }

    public static void loadEnchantmentInBootstrap(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.@NotNull Builder> event) {
        for (CustomEnchantment customEnchantment : customEnchantments.values()) {
            event.registry().register(
                    EnchantmentKeys.create(customEnchantment.getKey()),
                    b -> b.description(customEnchantment.getName())
                            .supportedItems(event.getOrCreateTag(customEnchantment.getSupportedItems()))
                            .anvilCost(customEnchantment.getAnvilCost())
                            .maxLevel(customEnchantment.getMaxLevel())
                            .weight(customEnchantment.getWeight())
                            .minimumCost(customEnchantment.getMinimumCost())
                            .maximumCost(customEnchantment.getMaximalmCost())
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
        }
    }

    public static void postInit() {
        for (CustomEnchantment customEnchantment : customEnchantments.values()) {
            Key key = customEnchantment.getKey();
           for (int level = 1; level <= customEnchantment.getMaxLevel(); level++)
                CustomItemRegistry.register(
                        key.asMinimalString() + level,
                        customEnchantment.getEnchantedBookItem(level)
                );

            if (customEnchantment instanceof Listener listener) {
                OMCPlugin.registerEvents(listener);
            }
        }
    }

    public static CustomEnchantment getCustomEnchantmentByKey(Key key) {
        return customEnchantments.get(key);
    }

    public static CustomEnchantment getCustomEnchantmentByName(String name) {
        return customEnchantments.get(new NamespacedKey(OMCPlugin.getInstance(), name));
    }
}
