package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.listeners.registry.DreamMobDamageListener;
import fr.openmc.core.features.dream.registries.mobs.*;
import fr.openmc.core.features.dream.registries.mobs.listeners.MudBeachMobSpawningListener;
import fr.openmc.core.features.dream.registries.mobs.listeners.PlainsMobSpawningListener;
import fr.openmc.core.features.dream.registries.mobs.listeners.SoulForestMobSpawningListener;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.mobs.CustomMobEntry;

/**
 * Gestionnaire de l'apparition des mobs dans la Dimension des Rêves.
 *
 * <p>Cette classe initialise les probabilités d'apparition des mobs ainsi que
 * l'enregistrement des listeners correspondants.</p>
 */
public class DreamMobsRegistry extends SubRegistry<String, CustomMobEntry> {

    public final CustomMobEntry DREAM_STRAY = register(new CustomMobEntry(
            "omc_dream:dream_stray",
            DreamStray::new
    ));

    public final CustomMobEntry DREAM_CREAKING = register(new CustomMobEntry(
            "omc_dream:dream_creaking",
            DreamCreaking::new
    ));

    public final CustomMobEntry DREAM_SPIDER = register(new CustomMobEntry(
            "omc_dream:dream_spider",
            DreamSpider::new
    ));

    public final CustomMobEntry SOUL = register(new CustomMobEntry(
            "omc_dream:soul",
            Soul::new
    ));

    public final CustomMobEntry BREEZY = register(new CustomMobEntry(
            "omc_dream:breezy",
            Breezy::new
    ));

    public final CustomMobEntry DREAM_PHANTOM = register(new CustomMobEntry(
            "omc_dream:dream_phantom",
            DreamPhantom::new
    ));

    public final CustomMobEntry CORRUPTED_TADPOLE = register(new CustomMobEntry(
            "omc_dream:corrupted_tadpole",
            CorruptedTadpole::new
    ));

    public final CustomMobEntry CRAZY_FROG = register(new CustomMobEntry(
            "omc_dream:crazy_frog",
            CrazyFrog::new
    ));

    @Override
    public void init() {
        OMCPlugin.registerEvents(
                PlainsMobSpawningListener::new,
                SoulForestMobSpawningListener::new,
                MudBeachMobSpawningListener::new,
                DreamMobDamageListener::new
        );
    }

    @Override
    public KeyedRegistry<String, ? super CustomMobEntry> getParentRegistry() {
        return OMCRegistry.CUSTOM_MOBS;
    }
}
