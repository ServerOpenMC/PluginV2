package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bat;

import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;

public class ExplosiveVampireBat extends AbstractVampireBat {

    public ExplosiveVampireBat(String id) {
        super(id,
                "Chauve-souris explosive"
        );
    }

    @Override
    public void playTravelParticles(Bat bat) {
        ParticleUtils.sendParticlePacket(
                Particle.FLAME,
                bat.getLocation(),
                32
        );

        ParticleUtils.sendParticlePacket(
                Particle.SMOKE,
                bat.getLocation(),
                32
        );
    }

    @Override
    public void onImpact(Bat bat, Player target, Location impactLocation) {
        World world = impactLocation.getWorld();

        Particle.EXPLOSION.builder()
                .location(impactLocation)
                .count(5)
                .offset(0.5, 0.5, 0.5)
                .receivers(40, true)
                .spawn();

        world.playSound(
                impactLocation,
                Sound.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE,
                2.0F,
                0.8F
        );

        for (Player player : world.getNearbyPlayers(impactLocation, 3.0)) {
            if (!canDamage(player)) continue;

            player.damage(4, bat);
        }
    }

    private boolean canDamage(Player player) {
        return !player.isDead()
                && player.getGameMode() != GameMode.CREATIVE
                && player.getGameMode() != GameMode.SPECTATOR;
    }
}
