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

/**
 * Gestionnaire de l'apparition des mobs dans la Dimension des Rêves.
 *
 * <p>Cette classe initialise les probabilités d'apparition des mobs ainsi que
 * l'enregistrement des listeners correspondants.</p>
 */
public class DreamMobsRegistry {

    public static void init() {
        OMCPlugin.registerEvents(
                new PlainsMobSpawningListener(),
                new SoulForestMobSpawningListener(),
                new MudBeachMobSpawningListener(),
                new CustomMobDeathListener(),
                new DreamMobDamageListener()
        );

        OMCRegistry.CUSTOM_MOBS.register(
                new CustomMobEntry(
                        "omc_dream:dream_stray",
                        DreamStray::new
                ),
                new CustomMobEntry(
                        "omc_dream:dream_creaking",
                        DreamCreaking::new
                ),
                new CustomMobEntry(
                        "omc_dream:dream_spider",
                        DreamSpider::new
                ),
                new CustomMobEntry(
                        "omc_dream:soul",
                        Soul::new
                ),
                new CustomMobEntry(
                        "omc_dream:breezy",
                        Breezy::new
                ),
                new CustomMobEntry(
                        "omc_dream:dream_phantom",
                        DreamPhantom::new
                ),
                new CustomMobEntry(
                        "omc_dream:corrupted_tadpole",
                        CorruptedTadpole::new
                ),
                new CustomMobEntry(
                        "omc_dream:crazy_frog",
                        CrazyFrog::new
                )
        );
    }
}
