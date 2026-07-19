package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bat;

import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevitationVampireBat extends AbstractVampireBat {

    public LevitationVampireBat(String id) {
        super(id,
                "Chauve-souris de lévitation"
        );
    }

    @Override
    public void playTravelParticles(Bat bat) {
        ParticleUtils.sendParticlePacket(
                Particle.END_ROD,
                bat.getLocation(),
                32
        );
    }

    @Override
    public void onImpact(Bat bat, Player target, Location impactLocation) {
        impactLocation.getWorld().spawn(
                impactLocation,
                AreaEffectCloud.class,
                c -> {
                    c.setRadius(4.0F);
                    c.setDuration(20 * 5);
                    c.setWaitTime(0);

                    c.setRadiusPerTick(-c.getRadius() / c.getDuration());

                    c.setParticle(Particle.ENTITY_EFFECT, Color.fromRGB(60, 160, 40));

                    c.addCustomEffect(
                            new PotionEffect(
                                    PotionEffectType.LEVITATION,
                                    20 * 5,
                                    1,
                                    false,
                                    true,
                                    true
                            ),
                            true
                    );
                }
        );


        if (target != null)
            target.getWorld().playSound(
                    target.getLocation(),
                    Sound.ENTITY_SHULKER_SHOOT,
                    SoundCategory.HOSTILE,
                    1.5F,
                    0.8F
            );

        Particle.END_ROD.builder()
                .location(impactLocation)
                .count(40)
                .offset(1.0, 1.5, 1.0)
                .extra(0.1)
                .receivers(40, true)
                .spawn();
    }
}
