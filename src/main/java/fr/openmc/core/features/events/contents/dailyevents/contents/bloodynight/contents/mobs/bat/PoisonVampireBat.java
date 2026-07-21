package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bat;

import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonVampireBat extends AbstractVampireBat {

    public PoisonVampireBat(String id) {
        super(id,
                TranslationManager.translation("feature.dailyevents.bloody_night.mob.poison_vampire_bat")
        );
    }

    @Override
    public void playTravelParticles(Bat bat) {
        Particle.ENTITY_EFFECT.builder()
                .location(bat.getLocation())
                .count(4)
                .offset(0.2, 0.2, 0.2)
                .extra(0.01)
                .data(Color.fromRGB(60, 160, 40))
                .receivers(32, true)
                .spawn();
    }

    @Override
    public void onImpact(Bat bat, Player target, Location impactLocation) {
        World world = impactLocation.getWorld();

        world.spawn(
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
                                    PotionEffectType.POISON,
                                    20 * 4,
                                    1,
                                    false,
                                    true,
                                    true
                            ),
                            true
                    );
                }
        );

        world.playSound(
                impactLocation,
                Sound.ENTITY_SPLASH_POTION_BREAK,
                SoundCategory.HOSTILE,
                1.5F,
                0.6F
        );

        Particle.ENTITY_EFFECT.builder()
                .location(impactLocation)
                .count(50)
                .offset(2.0, 1.0, 2.0)
                .extra(0.05)
                .data(Color.fromRGB(60, 160, 40))
                .receivers(40, true)
                .spawn();
    }
}
