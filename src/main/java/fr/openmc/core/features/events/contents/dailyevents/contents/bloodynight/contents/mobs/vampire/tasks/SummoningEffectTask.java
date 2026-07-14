package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks;

import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class SummoningEffectTask extends BukkitRunnable {
    private final VampireBoss boss;
    private final Location spawnLocation;
    private long elapsedTicks = 0;

    public static final long SUMMON_DURATION_TICKS = 20L * 60L; // 60 sec
    public static final long SUMMON_EFFECT_INTERVAL_TICKS = 20L * 5L; // 5 sec

    public SummoningEffectTask(VampireBoss boss, Location spawnLocation) {
        this.boss = boss;
        this.spawnLocation = spawnLocation;
    }

    @Override
    public void run() {
        if (elapsedTicks >= SUMMON_DURATION_TICKS) {
            cancel();
            boss.summon(spawnLocation);
            return;
        }

        playSummoningEffects(spawnLocation);

        elapsedTicks += SUMMON_EFFECT_INTERVAL_TICKS;
    }

    private void playSummoningEffects(Location center) {
        World world = center.getWorld();

        if (world == null) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 12; i++) {
            double angle = random.nextDouble(0, Math.PI * 2);
            double distance = random.nextDouble(3.0, 12.0);

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            Location lightningLocation = center.clone().add(
                    offsetX,
                    0,
                    offsetZ
            );

            lightningLocation.setY(world.getHighestBlockYAt(lightningLocation) + 1);

            world.strikeLightningEffect(lightningLocation);
        }

        ParticleUtils.spawnCubeParticles(
                center,
                Particle.EXPLOSION,
                3.0,
                3.0,
                3.0,
                15,
                20,
                null
        );

        ParticleUtils.spawnCubeParticles(
                center,
                Particle.RAID_OMEN,
                5.0,
                6.0,
                5.0,
                200,
                40,
                null
        );

        ParticleUtils.spawnCubeParticles(
                center,
                Particle.LARGE_SMOKE,
                4.0,
                4.0,
                4.0,
                100,
                50,
                null
        );

        world.playSound(
                center,
                Sound.ENTITY_WITHER_AMBIENT,
                SoundCategory.HOSTILE,
                10.0F,
                0.5F
        );

        world.playSound(
                center,
                Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.WEATHER,
                4.0F,
                0.3F
        );
    }
}