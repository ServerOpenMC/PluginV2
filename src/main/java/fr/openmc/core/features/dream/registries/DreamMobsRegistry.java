package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.listeners.registry.DreamMobDamageListener;
import fr.openmc.core.features.dream.listeners.registry.DreamMobLootListener;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.mobs.*;
import fr.openmc.core.features.dream.registries.mobs.listeners.MudBeachMobSpawningListener;
import fr.openmc.core.features.dream.registries.mobs.listeners.PlainsMobSpawningListener;
import fr.openmc.core.features.dream.registries.mobs.listeners.SoulForestMobSpawningListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de l'apparition des mobs dans la Dimension des Rêves.
 *
 * <p>Cette classe initialise les probabilités d'apparition des mobs ainsi que
 * l'enregistrement des listeners correspondants.</p>
 */
public class DreamMobsRegistry {

    public static final NamespacedKey mobKey = new NamespacedKey("openmc", "dream_mob");

    private static final Map<String, DreamMob> mobsByName = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(
                new PlainsMobSpawningListener(),
                new SoulForestMobSpawningListener(),
                new MudBeachMobSpawningListener(),
                new DreamMobLootListener(),
                new DreamMobDamageListener()
        );

        register(new DreamCreaking());
        register(new DreamSpider());
        register(new Soul());
        register(new DreamStray());
        register(new Breezy());
        register(new DreamPhantom());
        register(new CorruptedTadpole());
        register(new CrazyFrog());
    }

    public static void register(DreamMob mob) {
        if (mob instanceof Listener listener) {
            OMCPlugin.registerEvents(listener);
        }
        mobsByName.put(mob.getId(), mob);
    }

    public static boolean isDreamMob(Entity entity) {
        return entity.getPersistentDataContainer().has(mobKey);
    }

    public static DreamMob getByName(String name) {
        return mobsByName.get(name);
    }

    public static DreamMob getFromEntity(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!pdc.has(DreamMobsRegistry.mobKey, PersistentDataType.STRING)) return null;

        String mobId = pdc.get(DreamMobsRegistry.mobKey, PersistentDataType.STRING);
        return getByName(mobId);
    }
}
