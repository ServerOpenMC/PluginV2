package fr.openmc.core;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.registries.LifecycleRegistry;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class OMCRegistry {

    public static final CustomItemRegistry CUSTOM_ITEMS = new CustomItemRegistry();
    public static final CustomMobRegistry CUSTOM_MOBS = new CustomMobRegistry();
    public static final CustomEnchantmentRegistry CUSTOM_ENCHANTS = new CustomEnchantmentRegistry();
    public static final CustomLootTableRegistry CUSTOM_LOOT_TABLES = new CustomLootTableRegistry();
    public static final CustomAmbientRegistry CUSTOM_AMBIENTS = new CustomAmbientRegistry();

    private static final List<LifecycleRegistry> ALL = List.of(
            CUSTOM_ITEMS,
            CUSTOM_MOBS,
            CUSTOM_ENCHANTS,
            CUSTOM_LOOT_TABLES,
            CUSTOM_AMBIENTS
    );

    private OMCRegistry() {}

    public static void bootstrapAll(BootstrapContext context) {
        for (LifecycleRegistry r : OMCRegistry.ALL) {
            if (isOverridden(r, "bootstrap", BootstrapContext.class)) {
                try {
                    r.bootstrap(context);
                } catch (IOException e) {
                    OMCLogger.errorFormatted("Erreur lors du chargement du registre '{}' lors du bootstrap", r.getClass().getSimpleName());
                    OMCLogger.error(e.getMessage());
                }
                OMCLogger.successFormatted("Registre {} chargé pendant le bootstrap", r.getClass().getSimpleName());
            }
        }
    }

    public static void initAll() {
        for (LifecycleRegistry r : OMCRegistry.ALL) {
            if (isOverridden(r, "init")) {
                r.init();
                OMCLogger.successFormatted("Registre {} chargé pendant le runtime", r.getClass().getSimpleName());
            }
        }
    }

    public static void postInitAll() {
        for (LifecycleRegistry r : OMCRegistry.ALL) {
            if (isOverridden(r, "postInit")) {
                r.postInit();
                OMCLogger.successFormatted("Registre {} chargé après ItemsAdder", r.getClass().getSimpleName());
            }
        }
    }

    private static boolean isOverridden(LifecycleRegistry r, String methodName, Class<?>... args) {
        try {
            return !r.getClass()
                    .getMethod(methodName, args)
                    .getDeclaringClass()
                    .equals(LifecycleRegistry.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}