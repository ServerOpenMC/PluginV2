package fr.openmc.core;

import fr.openmc.core.features.city.CityFeaturesRegistry;
import fr.openmc.core.features.dream.registries.DreamFeaturesRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.DreamLootTableRegistry;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestFeaturesRegistry;
import fr.openmc.core.features.homes.HomeFeaturesRegistry;
import fr.openmc.core.features.shops.ShopFeaturesRegistry;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.registries.LifecycleRegistry;
import fr.openmc.core.lifecycle.registries.RegistryContext;
import fr.openmc.core.lifecycle.registries.RegistryLoadingType;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.features.FeaturesRegistry;
import fr.openmc.core.registry.hooks.HooksRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootboxRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import fr.openmc.core.registry.regions.CustomRegionRegistry;
import fr.openmc.core.registry.worldtemplates.WorldTemplateRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class OMCRegistry {
    // * Registre globaux
    public static FeaturesRegistry FEATURES;
    public static HooksRegistry HOOKS;

    public static CustomItemRegistry CUSTOM_ITEMS;
    public static CustomMobRegistry CUSTOM_MOBS;
    public static CustomEnchantmentRegistry CUSTOM_ENCHANTS;
    public static CustomLootTableRegistry CUSTOM_LOOT_TABLES;
    public static CustomAmbientRegistry CUSTOM_AMBIENTS;
    public static CustomLootboxRegistry CUSTOM_LOOTBOXES;
    public static CustomRegionRegistry CUSTOM_REGIONS;
    public static WorldTemplateRegistry WORLD_TEMPLATES;

    // * Registre des features
    public static WeeklyEventsRegistry WEEKLY_EVENTS;
    public static DailyEventsRegistry DAILY_EVENTS;

    // ** Registre concernant la feature de la Dimension des reves
    public static DreamFeaturesRegistry DREAM_FEATURES;
    public static DreamItemRegistry DREAM_ITEM;
    public static DreamMobsRegistry DREAM_MOB;
    public static DreamLootTableRegistry DREAM_LOOT_TABLE;

    // * Registre concernant la feature des villes
    public static CityFeaturesRegistry CITY_FEATURES;

    // * Registre concernant la feature des contests
    public static ContestFeaturesRegistry CONTEST_FEATURES;

    // * Registre concernant la feature des homes
    public static HomeFeaturesRegistry HOME_FEATURES;

    // * Registre concernant la feature des shops
    public static ShopFeaturesRegistry SHOP_FEATURES;

    private static final List<LifecycleRegistry> LOADED = new ArrayList<>();

    private static final List<RegistryContext> ALL = new ArrayList<>(List.of(
            new RegistryContext(
                    () -> HOOKS = new HooksRegistry(),
                    RegistryLoadingType.RUNTIME),
            new RegistryContext(
                    () -> CUSTOM_ITEMS = new CustomItemRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> CUSTOM_ENCHANTS = new CustomEnchantmentRegistry(),
                    RegistryLoadingType.BOOTSTRAP, RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> CUSTOM_LOOT_TABLES = new CustomLootTableRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> CUSTOM_AMBIENTS = new CustomAmbientRegistry(),
                    RegistryLoadingType.BOOTSTRAP, RegistryLoadingType.RUNTIME, RegistryLoadingType.NOT_LOADED_UNIT_TEST),
            new RegistryContext(
                    () -> CUSTOM_LOOTBOXES = new CustomLootboxRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(() -> CUSTOM_MOBS = new CustomMobRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(() -> WORLD_TEMPLATES = new WorldTemplateRegistry(),
                    RegistryLoadingType.BOOTSTRAP, RegistryLoadingType.RUNTIME),
            new RegistryContext(() -> WEEKLY_EVENTS = new WeeklyEventsRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(() -> DAILY_EVENTS = new DailyEventsRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(() -> CUSTOM_REGIONS = new CustomRegionRegistry(),
                    RegistryLoadingType.AFTER_IA),
            new RegistryContext(
                    () -> FEATURES = new FeaturesRegistry(),
                    RegistryLoadingType.RUNTIME, RegistryLoadingType.AFTER_IA)
    ));

    private OMCRegistry() {}

    public static void bootstrapAll(BootstrapContext context) {
        for (RegistryContext ctx : OMCRegistry.ALL) {
            if (isNotTyped(ctx, RegistryLoadingType.BOOTSTRAP)) continue;

            LifecycleRegistry r = load(ctx);
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
            if (isTyped(ctx, RegistryLoadingType.NOT_LOADED_UNIT_TEST) && OMCPlugin.isUnitTestVersion()) continue;
            if (isNotTyped(ctx, RegistryLoadingType.RUNTIME)) continue;

            LifecycleRegistry r = load(ctx);

            if (r instanceof HasListeners hasListeners)
                OMCPlugin.registerEvents(hasListeners.getListeners());

            r.init();
            OMCLogger.successFormatted("Registre {} chargé pendant le runtime", r.getClass().getSimpleName());
        }
    }

    public static void postInitAll() {
        for (RegistryContext ctx : OMCRegistry.ALL) {
            if (isTyped(ctx, RegistryLoadingType.NOT_LOADED_UNIT_TEST) && OMCPlugin.isUnitTestVersion()) continue;
            if (isNotTyped(ctx, RegistryLoadingType.AFTER_IA)) continue;

            LifecycleRegistry r = load(ctx);

            if (r instanceof HasListeners hasListeners)
                OMCPlugin.registerEvents(hasListeners.getListeners());

            r.postInit();
            OMCLogger.successFormatted("Registre {} chargé après ItemsAdder", r.getClass().getSimpleName());
        }
    }

    public static void stopAll() {
        for (LifecycleRegistry r : LOADED) {
            r.stop();
            OMCLogger.successFormatted("Registre {} stoppé", r.getClass().getSimpleName());
        }
        LOADED.clear();
    }

    private static LifecycleRegistry load(RegistryContext ctx) {
        return load(ctx.registry().get());
    }

    public static LifecycleRegistry load(LifecycleRegistry registry) {
        LOADED.add(registry);
        return registry;
    }

    private static boolean isNotTyped(RegistryContext ctx, RegistryLoadingType type) {
        return Arrays.stream(ctx.loadingTypes()).noneMatch(t -> t == type);
    }

    private static boolean isTyped(RegistryContext ctx, RegistryLoadingType type) {
        return Arrays.stream(ctx.loadingTypes()).anyMatch(t -> t == type);
    }
}
