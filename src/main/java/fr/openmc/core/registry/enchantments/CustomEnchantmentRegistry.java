package fr.openmc.core.registry.enchantments;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.dream.registries.enchantements.DreamSleeper;
import fr.openmc.core.features.dream.registries.enchantements.Experientastic;
import fr.openmc.core.features.dream.registries.enchantements.Soulbound;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;

@SuppressWarnings("UnstableApiUsage")
public class CustomEnchantmentRegistry extends Registry<Key, CustomEnchantment> {

    @Override
    public void bootstrap(BootstrapContext context) {
        registerAll(
                new Soulbound(),
                new Experientastic(),
                new DreamSleeper()
        );

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose()
                .newHandler(this::loadEnchantmentInBootstrap));

    }

    private void loadEnchantmentInBootstrap(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event) {
        for (CustomEnchantment customEnchantment : values()) {
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

    @Override
    public void postInit() {
        for (CustomEnchantment e : values()) {

            for (int level = 1; level <= e.getMaxLevel(); level++) {
                OMCRegistry.CUSTOM_ITEMS.register(
                        e.getKey().asMinimalString() + level,
                        e.getEnchantedBookItem(level)
                );
            }

            if (e instanceof Listener listener) {
                OMCPlugin.registerEvents(listener);
            }
        }
    }

    public void registerAll(CustomEnchantment... enchantments) {
        for (CustomEnchantment e : enchantments) {
            register(e.getKey(), e);
        }
    }
}