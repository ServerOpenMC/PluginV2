package fr.openmc.core.registry.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.AncientVillager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.ChickenJockey;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.PoissonSteve;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.SeaGuard;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomMobRegistry extends Registry<String, CustomMobEntry> implements KeyedRegistry<String, CustomMobEntry> {

    public static final NamespacedKey CUSTOM_MOB_KEY =
            new NamespacedKey("openmc", "custom_mob");

    // ** REGISTER MOBS **
    public final CustomMobEntry SEA_GUARD = register(new CustomMobEntry(
            "omc_daily_events:sea_guard",
            SeaGuard::new
    ));

    public final CustomMobEntry CHICKEN_JOCKEY = register(new CustomMobEntry(
            "omc_daily_events:chicken_jockey",
            ChickenJockey::new
    ));

    public final CustomMobEntry ANCIENT_VILLAGER = register(new CustomMobEntry(
            "omc_daily_events:ancient_villager",
            AncientVillager::new
    ));

    public final CustomMobEntry POISSON_STEVE = register(new CustomMobEntry(
            "omc_daily_events:poisson_steve",
            PoissonSteve::new
    ));

    @Override
    public String key(CustomMobEntry registryObject) {
        return registryObject.id();
    }

    @Override
    public CustomMobEntry register(CustomMobEntry mob) {
        if (mob.factory().apply(mob.id()) instanceof Listener listener) {
            OMCPlugin.registerEvents(listener);
        }
        return register(mob.id(), mob);
    }

    public CustomMob<?> getMob(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!pdc.has(CUSTOM_MOB_KEY, PersistentDataType.STRING)) return null;

        String mobId = pdc.get(CUSTOM_MOB_KEY, PersistentDataType.STRING);
        return get(mobId).map(CustomMobEntry::getMob).orElse(null);
    }

    public static boolean isCustomMob(Entity entity) {
        return entity.getPersistentDataContainer().has(CUSTOM_MOB_KEY);
    }
}
