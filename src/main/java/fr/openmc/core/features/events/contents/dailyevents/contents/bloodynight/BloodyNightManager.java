package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bloodytypes.AncientMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bloodytypes.CorruptedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bloodytypes.CursedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bloodytypes.EnragedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireSlave;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public class BloodyNightManager {
    // * CONSTANTES
    public static final NamespacedKey RAID_MONSTER_KEY = new NamespacedKey("omc_daily_events", "raid_monster");

    public static final long VAMPIRE_SPAWN_TIME = 20L * 60L * 15; // 15 min
    public static final long RAID_INTERVAL = 20L * 60L * 2; // 2 min

    private static BukkitTask raidTask;
    private static BukkitTask vampireTask;
    private static long lastTime;

    public static void start(BloodyNightEvent event) {
        World world = Bukkit.getWorld(event.getWorldEvent());
        if (world == null) return;

        // * Programmation des raids
        raidTask = Bukkit.getScheduler().runTaskTimer(
                OMCPlugin.getInstance(),
                () -> BloodyNightRaidManager.startRaid(world),
                20L * 10,
                RAID_INTERVAL
        );

        // * Programmation du boss Vampire
        Location vampireSpawnLocation = LocationUtils.getSafeNearbySurface(
                LocationUtils.randomLocation(
                        world.getSpawnLocation(),
                        Math.min(10000, world.getWorldBorder().getSize() / 2.0)
                ),
                50);

        vampireTask = Bukkit.getScheduler().runTaskLater(
                OMCPlugin.getInstance(),
                () -> OMCRegistry.CUSTOM_MOBS.VAMPIRE_BOSS.spawn(vampireSpawnLocation),
                VAMPIRE_SPAWN_TIME
        );

        // * Modification des monstres déjà présent dans le monde (uniquement ceux chargé)
        applyCorruptedMonsters(world);

        // * Gamerules personalisée
        lastTime = world.getTime();
        world.setTime(21000);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
    }

    public static void stop(BloodyNightEvent event) {
        World world = Bukkit.getWorld(event.getWorldEvent());
        if (world == null) return;

        // * Fin des raids
        if (raidTask != null) {
            raidTask.cancel();
            raidTask = null;
        }

        // * Fin des raids
        if (vampireTask != null) {
            vampireTask.cancel();
            vampireTask = null;
        }

        // * Supression des monstres devant etre supprimé (ex ceux qui vient des raids)
        deleteRaidMonsters(world);
        deleteVampireMonsters(world);

        // * Modification des monstres déjà présent dans le monde (uniquement ceux chargé)
        desactivateCorruptedMonsters(world);

        // * Gamerules personalisée
        world.setTime(lastTime);
        world.setGameRule(GameRules.ADVANCE_TIME, true);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, true);
    }

    /**
     * Désactive les mobs chargé dans le monde et les remet à leur état normal
     * @param world le monde ciblé par la nuit sanglante
     */
    private static void desactivateCorruptedMonsters(World world) {
        for (Entity entity : world.getEntities()) {
            desactivateCorruptedMonster(entity);
        }
    }

    /**
     * Désactive le mob et le remet à son état normal
     * @param entity l'entité ciblé à désactiver
     */
    public static void desactivateCorruptedMonster(Entity entity) {
        if (!(entity instanceof Monster monster)) return;

        CustomMob<?> cm = OMCRegistry.CUSTOM_MOBS.getMob(monster);
        if (cm instanceof CorruptedMonster corruptedMonster) {
            corruptedMonster.resetToDefault(monster);
        } else if (cm instanceof CursedMonster cursedMonster) {
            cursedMonster.resetToDefault(monster);
        } else if (cm instanceof EnragedMonster enragedMonster) {
            enragedMonster.resetToDefault(monster);
        } else if (cm instanceof AncientMonster ancientMonster) {
            ancientMonster.resetToDefault(monster);
        }
    }

    /**
     * Supprime tout les monstres spawné par les raids
     * @param world le monde ciblé par la nuit sanglante
     */
    private static void deleteRaidMonsters(World world) {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Monster monster)) continue;
            if (!(entity.getPersistentDataContainer().has(RAID_MONSTER_KEY))) continue;

            monster.remove();
        }
    }

    /**
     * Supprime tout les monstres spawné par le vampire
     * @param world le monde ciblé par la nuit sanglante
     */
    private static void deleteVampireMonsters(World world) {
        for (Entity entity : world.getEntities()) {
            CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);
            if (customMob == null) continue;
            if (customMob instanceof VampireBoss || customMob instanceof VampireSlave)
                entity.remove();
        }
    }

    public static final double CORRUPTED_CHANCE = 0.5;
    public static final double ENRAGED_CHANCE = 0.3;
    public static final double CURSED_CHANCE = 0.15;
    public static final double ANCIENT_CHANCE = 0.05;

    /**
     * Applique l'effet bloody sur un monstre
     * - Corrompu - 60%
     * - Enragé - 30%
     * - Maudit - 15%
     * - Ancien - 5%
     * @param entity l'entité à boost
     */
    public static void applyBloodyMonster(LivingEntity entity) {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random < CORRUPTED_CHANCE) {
            OMCRegistry.CUSTOM_MOBS.CORRUPTED_MONSTER.apply(entity);
        } else if (random < CORRUPTED_CHANCE + ENRAGED_CHANCE) {
            OMCRegistry.CUSTOM_MOBS.ENRAGED_MONSTER.apply(entity);
        } else if (random < CORRUPTED_CHANCE + ENRAGED_CHANCE + CURSED_CHANCE) {
            OMCRegistry.CUSTOM_MOBS.CURSED_MONSTER.apply(entity);
        } else {
            OMCRegistry.CUSTOM_MOBS.ANCIENT_MONSTER.apply(entity);
        }
    }

    /**
     * Boost touts les monstres présent dans le monde
     * @param world le monde où se passe la blood moon
     */
    private static void applyCorruptedMonsters(World world) {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Monster monster)) continue;

            OMCRegistry.CUSTOM_MOBS.CORRUPTED_MONSTER.apply(monster);
        }
    }
}
