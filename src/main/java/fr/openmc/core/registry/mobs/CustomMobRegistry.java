package fr.openmc.core.registry.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.AncientMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.CorruptedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.CursedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.EnragedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.*;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.kraken.Kraken;
import fr.openmc.core.registry.mobs.task.MobBossbarUpdater;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    public final CustomMobEntry LEVIATHAN = register(new CustomMobEntry(
            "omc_daily_events:leviathan",
            Leviathan::new
    ));

    public final CustomMobEntry ANGRY_WITCH = register(new CustomMobEntry(
            "omc_daily_events:angry_witch",
            AngryWitch::new
    ));

    public final CustomMobEntry KRAKEN = register(new CustomMobEntry(
            "omc_daily_events:kraken",
            Kraken::new
    ));

    public final CustomMobEntry GIANT_ELDER_GUARDIAN = register(new CustomMobEntry(
            "omc_daily_events:giant_elder_guardian",
            GiantElderGuardian::new
    ));

    public final CustomMobEntry ANCIENT_MONSTER = register(new CustomMobEntry(
            "omc_daily_events:ancient_monster",
            AncientMonster::new
    ));

    public final CustomMobEntry CURSED_MONSTER = register(new CustomMobEntry(
            "omc_daily_events:cursed_monster",
            CursedMonster::new
    ));

    public final CustomMobEntry ENRAGED_MONSTER = register(new CustomMobEntry(
            "omc_daily_events:enraged_monster",
            EnragedMonster::new
    ));

    public final CustomMobEntry CORRUPTED_MONSTER = register(new CustomMobEntry(
            "omc_daily_events:corrupted_monster",
            CorruptedMonster::new
    ));

    public final CustomMobEntry VAMPIRE_BOSS = register(new CustomMobEntry(
            "omc_daily_events:vampire_boss",
            VampireBoss::new
    ));

    public final static Set<UUID> HAS_BOSSBAR = new HashSet<>();

    @Override
    public void postInit() {
        new MobBossbarUpdater().runTaskTimer(
                OMCPlugin.getInstance(),
                0L,
                20L
        );
    }

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
