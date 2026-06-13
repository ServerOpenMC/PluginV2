package fr.openmc.core;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.registries.LifecycleRegistry;
import fr.openmc.core.bootstrap.registries.RegistryContext;
import fr.openmc.core.bootstrap.registries.RegistryLoadingType;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootboxRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class OMCRegistry {

    public static CustomItemRegistry CUSTOM_ITEMS;
    public static CustomMobRegistry CUSTOM_MOBS;
    public static CustomEnchantmentRegistry CUSTOM_ENCHANTS;
    public static CustomLootTableRegistry CUSTOM_LOOT_TABLES;
    public static CustomAmbientRegistry CUSTOM_AMBIENTS;
    public static CustomLootboxRegistry CUSTOM_LOOTBOXES;


    private static final List<RegistryContext> ALL = List.of(
            new RegistryContext(
                    () -> CUSTOM_ITEMS = new CustomItemRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(() -> CUSTOM_MOBS = new CustomMobRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> CUSTOM_ENCHANTS = new CustomEnchantmentRegistry(),
                    RegistryLoadingType.BOOTSTRAP, RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> CUSTOM_LOOT_TABLES = new CustomLootTableRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> CUSTOM_AMBIENTS = new CustomAmbientRegistry(),
                    RegistryLoadingType.BOOTSTRAP),
            new RegistryContext(
                    () -> CUSTOM_LOOTBOXES = new CustomLootboxRegistry(),
                    RegistryLoadingType.AFTER_IA)
    );

    private OMCRegistry() {}

    public static void bootstrapAll(BootstrapContext context) {
        for (RegistryContext ctx : OMCRegistry.ALL) {
            if (Arrays.stream(ctx.loadingTypes())
                    .noneMatch(t -> t == RegistryLoadingType.BOOTSTRAP)) continue;

            LifecycleRegistry r = ctx.registry().get();
            try {
                r.bootstrap(context);
            } catch (IOException e) {
                OMCLogger.errorFormatted("Erreur lors du chargement du registre '{}' lors du bootstrap", r.getClass().getSimpleName());
                OMCLogger.error(e.getMessage());
            }
            OMCLogger.successFormatted("Registre {} chargé pendant le bootstrap", r.getClass().getSimpleName());
        }
    }

    public static void initAll() {
        for (RegistryContext ctx : OMCRegistry.ALL) {
            if (Arrays.stream(ctx.loadingTypes())
                    .noneMatch(t -> t == RegistryLoadingType.RUNTIME)) continue;

            LifecycleRegistry r = ctx.registry().get();
            r.init();
            OMCLogger.successFormatted("Registre {} chargé pendant le runtime", r.getClass().getSimpleName());
        }
    }

    public static void postInitAll() {
        for (RegistryContext ctx : OMCRegistry.ALL) {
            if (Arrays.stream(ctx.loadingTypes())
                    .noneMatch(t -> t == RegistryLoadingType.AFTER_IA)) continue;

            LifecycleRegistry r = ctx.registry().get();
            r.postInit();
            OMCLogger.successFormatted("Registre {} chargé après ItemsAdder", r.getClass().getSimpleName());
        }
    }
}