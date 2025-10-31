package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.features.dream.registries.enchantements.DreamSleeper;
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
            new DreamSleeper()
    );

    private final static HashMap<Key, Enchantment> dreamEnchantment = new HashMap<>();

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
            dreamEnchantment.put(key, enchantment);

            if (dreamEnchant instanceof Listener listener) {
                OMCPlugin.registerEvents(listener);
            }
        }
    }

    public static Enchantment getDreamEnchantment(Key key) {
        return dreamEnchantment.get(key);
    }
}
