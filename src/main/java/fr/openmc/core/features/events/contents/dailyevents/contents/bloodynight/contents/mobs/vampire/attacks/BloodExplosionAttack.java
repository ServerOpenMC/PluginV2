package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.attacks;

import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.registry.mobs.MobAttack;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.*;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BloodExplosionAttack implements MobAttack {

    private final VampireBoss boss;

    public BloodExplosionAttack(VampireBoss boss) {
        this.boss = boss;
    }

    @Override
    public void execute() {
        Mannequin mannequin = boss.getMannequin();

        if (mannequin == null || !mannequin.isValid() || mannequin.isDead()) return;

        Location center = mannequin.getLocation()
                .clone()
                .add(0, mannequin.getHeight() / 3.0, 0);

        spawnRadialParticles(center);
        damageNearbyPlayers(mannequin);

        World world = mannequin.getWorld();

        world.playSound(
                center,
                Sound.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE,
                3.0F,
                0.6F
        );

        world.playSound(
                center,
                Sound.ENTITY_WITHER_HURT,
                SoundCategory.HOSTILE,
                2.0F,
                0.5F
        );
    }

    private void damageNearbyPlayers(Mannequin mannequin) {
        Collection<Player> players = mannequin.getWorld().getNearbyPlayers(
                mannequin.getLocation(),
                20
        );

        for (Player player : players) {
            if (!canDamage(player)) {
                continue;
            }

            double newHealth = player.getHealth() * 0.7;

            player.setHealth(Math.max(1.0, newHealth));

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_PLAYER_HURT,
                    SoundCategory.PLAYERS,
                    1.0F,
                    0.7F
            );
        }
    }

    private boolean canDamage(Player player) {
        return player.isValid()
                && !player.isDead()
                && player.getGameMode() != GameMode.CREATIVE
                && player.getGameMode() != GameMode.SPECTATOR;
    }

    /**
     * Fait apparaitre des particules de sorte qui partent dans toutes les directions d'un cercle en 2D
     * Math
     * @param center le mob
     */
    private void spawnRadialParticles(Location center) {
        Collection<Player> receivers =
                center.getNearbyEntitiesByType(
                        Player.class,
                        50
                );

        if (receivers.isEmpty()) return;

        int directions = 64;
        double particleSpeed = 1.2;

        double[] heightOffsets = {
                -2.0,
                -1.0,
                0.0,
                1.0,
                2.0
        };

        for (double heightOffset : heightOffsets) {
            Location particleOrigin = center.clone().add(
                    0,
                    heightOffset,
                    0
            );

            for (int i = 0; i < directions; i++) {
                double angle = (Math.PI * 2 * i) / directions;

                double directionX = Math.cos(angle);
                double directionZ = Math.sin(angle);

                for (Player player : receivers) {
                    ParticleUtils.sendParticlePacket(
                            player,
                            Particle.DUST,
                            particleOrigin,
                            0,
                            directionX,
                            0.0,
                            directionZ,
                            particleSpeed,
                            new Particle.DustOptions(Color.RED, 1.0f)
                    );

                    ParticleUtils.sendParticlePacket(
                            player,
                            Particle.LARGE_SMOKE,
                            particleOrigin,
                            0,
                            directionX,
                            0.0,
                            directionZ,
                            particleSpeed * 0.7,
                            null
                    );
                }
            }
        }

        ParticleUtils.spawnCubeParticles(
                center,
                Particle.EXPLOSION,
                1.5,
                2.5,
                1.5,
                20,
                50,
                null
        );
    }
}
