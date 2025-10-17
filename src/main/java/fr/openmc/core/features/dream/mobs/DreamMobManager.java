package fr.openmc.core.features.dream.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.mobs.listeners.DreamMobLootListener;
import fr.openmc.core.features.dream.mobs.listeners.PlainsMobSpawningListener;
import fr.openmc.core.features.dream.mobs.listeners.SoulForestMobSpawningListener;
import fr.openmc.core.features.dream.mobs.mobs.DreamCreaking;
import fr.openmc.core.features.dream.mobs.mobs.DreamSpider;
import fr.openmc.core.features.dream.mobs.mobs.Soul;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
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
public class DreamMobManager {

    public static final NamespacedKey mobKey = new NamespacedKey("openmc", "dream_mob");

    private static final Map<String, DreamMob> mobsByName = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(
                new PlainsMobSpawningListener(),
                new SoulForestMobSpawningListener(),
                new DreamMobLootListener()
        );

        register(new DreamCreaking());
        register(new DreamSpider());
        register(new Soul());
    }

    public static void register(DreamMob mob) {
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
        if (!pdc.has(DreamMobManager.mobKey, PersistentDataType.STRING)) return null;

        String mobId = pdc.get(DreamMobManager.mobKey, PersistentDataType.STRING);
        return getByName(mobId);
    }
}
