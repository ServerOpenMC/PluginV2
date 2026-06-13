package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.listeners.registry.DreamMobDamageListener;
import fr.openmc.core.features.dream.registries.mobs.*;
import fr.openmc.core.features.dream.registries.mobs.listeners.MudBeachMobSpawningListener;
import fr.openmc.core.features.dream.registries.mobs.listeners.PlainsMobSpawningListener;
import fr.openmc.core.features.dream.registries.mobs.listeners.SoulForestMobSpawningListener;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.registry.mobs.listeners.CustomMobDeathListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Gestionnaire de l'apparition des mobs dans la Dimension des Rêves.
 *
 * <p>Cette classe initialise les probabilités d'apparition des mobs ainsi que
 * l'enregistrement des listeners correspondants.</p>
 */
public class DreamMobsRegistry {

    public static final CustomMobEntry DREAM_STRAY = create(new CustomMobEntry(
            "omc_dream:dream_stray",
            DreamStray::new
    ));

    public static final CustomMobEntry DREAM_CREAKING = create(new CustomMobEntry(
            "omc_dream:dream_creaking",
            DreamCreaking::new
    ));

    public static final CustomMobEntry DREAM_SPIDER = create(new CustomMobEntry(
            "omc_dream:dream_spider",
            DreamSpider::new
    ));

    public static final CustomMobEntry SOUL = create(new CustomMobEntry(
            "omc_dream:soul",
            Soul::new
    ));

    public static final CustomMobEntry BREEZY = create(new CustomMobEntry(
            "omc_dream:breezy",
            Breezy::new
    ));

    public static final CustomMobEntry DREAM_PHANTOM = create(new CustomMobEntry(
            "omc_dream:dream_phantom",
            DreamPhantom::new
    ));

    public static final CustomMobEntry CORRUPTED_TADPOLE = create(new CustomMobEntry(
            "omc_dream:corrupted_tadpole",
            CorruptedTadpole::new
    ));

    public static final CustomMobEntry CRAZY_FROG = create(new CustomMobEntry(
            "omc_dream:crazy_frog",
            CrazyFrog::new
    ));

    public static Set<CustomMobEntry> DREAM_MOB_REGISTRY;

    private static CustomMobEntry create(CustomMobEntry entry) {
        if (DREAM_MOB_REGISTRY == null)
            DREAM_MOB_REGISTRY = new HashSet<>();

        DREAM_MOB_REGISTRY.add(entry);
        return entry;
    }
    public static void init() {
        OMCPlugin.registerEvents(
                new PlainsMobSpawningListener(),
                new SoulForestMobSpawningListener(),
                new MudBeachMobSpawningListener(),
                new CustomMobDeathListener(),
                new DreamMobDamageListener()
        );

        OMCRegistry.CUSTOM_MOBS.register(DREAM_MOB_REGISTRY);
    }
}
