package fr.openmc.core;

import fr.openmc.core.bootstrap.registries.BootstrapRegistry;
import fr.openmc.core.bootstrap.registries.LifecycleRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;

import java.util.List;

public final class OMCRegistry {

    public static final CustomItemRegistry CUSTOM_ITEMS = new CustomItemRegistry();
    public static final CustomEnchantmentRegistry CUSTOM_ENCHANTS = new CustomEnchantmentRegistry();
    public static final CustomLootTableRegistry CUSTOM_LOOT_TABLES = new CustomLootTableRegistry();

    private static final List<LifecycleRegistry> ALL = List.of(
            CUSTOM_ITEMS,
            CUSTOM_ENCHANTS,
            CUSTOM_LOOT_TABLES
    );

    private OMCRegistry() {}

    public static void bootstrapAll() {
        for (LifecycleRegistry r : OMCRegistry.ALL) {
            if (r instanceof BootstrapRegistry b) {
                b.bootstrap();
            }
        }
    }

    public static void initAll() {
        for (LifecycleRegistry r : OMCRegistry.ALL) {
            r.init();
        }
    }

    public static void postInitAll() {
        for (LifecycleRegistry r : OMCRegistry.ALL) {
            r.postInit();
        }
    }
}