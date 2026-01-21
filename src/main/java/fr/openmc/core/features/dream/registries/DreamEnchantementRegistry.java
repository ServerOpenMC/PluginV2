package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.features.dream.registries.enchantements.DreamSleeper;
import fr.openmc.core.features.dream.registries.enchantements.Experientastic;
import fr.openmc.core.features.dream.registries.enchantements.Soulbound;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class DreamEnchantementRegistry {

    private final static Set<DreamEnchantment> DREAM_ENCHANTMENT_REGISTRY = Set.of(
            new DreamSleeper(),
            new Experientastic(),
            new Soulbound()
    );

    private final static HashMap<Key, DreamEnchantment> dreamEnchantments = new HashMap<>();
    private final static HashMap<Key, Enchantment> enchantments = new HashMap<>();

    public static void loadEnchantmentInBootstrap(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.@NotNull Builder> event) {
        for (DreamEnchantment dreamEnchantment : DREAM_ENCHANTMENT_REGISTRY) {
            event.registry().register(
                    EnchantmentKeys.create(dreamEnchantment.getKey()),
                    b -> b.description(dreamEnchantment.getName())
                            .supportedItems(event.getOrCreateTag(dreamEnchantment.getSupportedItems()))
                            .anvilCost(dreamEnchantment.getAnvilCost())
                            .maxLevel(dreamEnchantment.getMaxLevel())
                            .weight(dreamEnchantment.getWeight())
                            .minimumCost(dreamEnchantment.getMinimumCost())
                            .maximumCost(dreamEnchantment.getMaximalmCost())
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
        }
    }

    public static void init() {
        Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess
                .registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT);

        for (DreamEnchantment dreamEnchant : DREAM_ENCHANTMENT_REGISTRY) {
            Key key = dreamEnchant.getKey();
            Enchantment enchantment = enchantmentRegistry.getOrThrow(
                    RegistryKey.ENCHANTMENT.typedKey(key)
            );
            dreamEnchantments.put(key, dreamEnchant);
            enchantments.put(key, enchantment);

            for (int level = 1; level <= dreamEnchant.getMaxLevel(); level++)
                DreamItemRegistry.register(
                        key.asMinimalString() + level,
                        dreamEnchant.getEnchantedBookItem(level)
                );

            if (dreamEnchant instanceof Listener listener) {
                OMCPlugin.registerEvents(listener);
            }
        }
    }

    public static DreamEnchantment getDreamEnchantment(Key key) {
        return dreamEnchantments.get(key);
    }

    public static Enchantment getEnchantment(Key key) {
        return enchantments.get(key);
    }
}
