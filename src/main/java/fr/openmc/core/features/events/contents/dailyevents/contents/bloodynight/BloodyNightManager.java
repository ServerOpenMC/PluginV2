package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.AncientMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.CorruptedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.CursedMonster;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.EnragedMonster;
import fr.openmc.core.registry.mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

// todo: tester si les mobs non chargé sont bien converti en monstre buffé
// todo: inverse aussi si les buffé disparaiseent bien
// todo: faire boss vampire (abilité chauve souris explosante, empoissonante, ...)
public class BloodyNightManager {
    public static final NamespacedKey RAID_MONSTER_KEY = new NamespacedKey("omc_daily_events", "raid_monster");
    private static BukkitTask raidTask;
    private static long lastTime;

    public static void start(BloodyNightEvent event) {
        World world = Bukkit.getWorld(event.getWorldEvent());
        if (world == null) return;

        // * Programmation des raids
        raidTask = Bukkit.getScheduler().runTaskTimer(
                OMCPlugin.getInstance(),
                () -> BloodyNightRaidManager.startRaid(world),
                20L * 10,
                20L * 60L * 2 // 2 min
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

        // * Supression des monstres devant etre supprimé (ex ceux qui vient des raids)
        deleteRaidMonsters(world);

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
     * Applique l'effet bloody sur un monstre
     * - Corrompu - 60%
     * - Enragé - 30%
     * - Maudit - 15%
     * - Ancien - 5%
     * @param entity l'entité à boost
     */
    public static void applyBloodyMonster(LivingEntity entity) {
        double random = ThreadLocalRandom.current().nextDouble();

        System.out.println(random);

        //todo: sfx particule
        if (random < 0.60) {
            OMCRegistry.CUSTOM_MOBS.CORRUPTED_MONSTER.apply(entity);
        } else if (random < 0.90) {
            OMCRegistry.CUSTOM_MOBS.ENRAGED_MONSTER.apply(entity);
        } else if (random < 0.95) {
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
