package fr.openmc.core.registry.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomMobRegistry extends Registry<String, CustomMobEntry> implements KeyedRegistry<String, CustomMobEntry> {

    public static final NamespacedKey CUSTOM_MOB_KEY =
            new NamespacedKey("openmc", "custom_mob");

    @Override
    public void postInit() {
        // ** REGISTER MOBS **
    }

    @Override
    public String key(CustomMobEntry registryObject) {
        return registryObject.id();
    }

    @Override
    public void register(CustomMobEntry mob) {
        if (mob.factory().apply(mob.id()) instanceof Listener listener) {
            OMCPlugin.registerEvents(listener);
        }
        register(mob.id(), mob);
    }

    public CustomMob<?> getMob(String id) {
        CustomMobEntry entry = get(id);
        return entry != null ? entry.factory().apply(id) : null;
    }

    public CustomMob<?> getMob(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!pdc.has(CUSTOM_MOB_KEY, PersistentDataType.STRING)) return null;

        String mobId = pdc.get(CUSTOM_MOB_KEY, PersistentDataType.STRING);
        return getMob(mobId);
    }

    public static boolean isCustomMob(Entity entity) {
        return entity.getPersistentDataContainer().has(CUSTOM_MOB_KEY);
    }
}
