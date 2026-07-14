package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks.SummoningEffectTask;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobAttribute;
import fr.openmc.core.registry.mobs.options.MobBossbarImpl;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;

import java.util.UUID;

import static fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks.SummoningEffectTask.SUMMON_EFFECT_INTERVAL_TICKS;

@SuppressWarnings("UnstableApiUsage")
public class VampireBoss extends CustomMob<Mannequin> implements MobBossbarImpl {
    public VampireBoss(String id) {
        super(id,
                "Vampire",
                Mannequin.class,
                1000,
                20,
                0.1,
                new CustomMobAttribute(Attribute.SCALE, 20),
                new CustomMobAttribute(Attribute.KNOCKBACK_RESISTANCE, 1)
        );
    }

    @Override
    public Mannequin spawn(Location spawnLocation) {
        startSummoning(spawnLocation);
        return null;
    }

    private void startSummoning(Location at) {
        World world = at.getWorld();
        for (Player player : world.getPlayers()) {
            player.sendMessage(TranslationManager.translation(
                    "feature.dailyevents.bloody_night.vampire_boss.coming"
            ));
        }

        new SummoningEffectTask(this, at).runTaskTimer(
                OMCPlugin.getInstance(),
                0L,
                SUMMON_EFFECT_INTERVAL_TICKS
        );
    }

    public void summon(Location at) {
        Mannequin mannequin = this.getPreBuildMob(at);
        mannequin.setDescription(Component.empty());
        mannequin.setProfile(ResolvableProfile.resolvableProfile()
                .uuid(UUID.fromString("2add34b4-2d09-4204-a458-6251b0d24661"))
                .build()
        );

        World world = at.getWorld();

        ParticleUtils.spawnCubeParticles(
                at.clone().add(0, 2, 0),
                Particle.EXPLOSION_EMITTER,
                1.0,
                2.0,
                1.0,
                10,
                100,
                null
        );

        ParticleUtils.spawnCubeParticles(
                at.clone().add(0, 2, 0),
                Particle.LAVA,
                3.0,
                5.0,
                3.0,
                200,
                100,
                null
        );

        world.playSound(
                at,
                Sound.ENTITY_WITHER_SPAWN,
                SoundCategory.HOSTILE,
                5.0F,
                0.6F
        );

        for (Player player : world.getPlayers()) {
            player.sendMessage(
                    TranslationManager.translation(
                            "feature.dailyevents.bloody_night.vampire_boss.summoned",
                            Component.text(at.getX(), NamedTextColor.RED),
                            Component.text(at.getY(), NamedTextColor.RED),
                            Component.text(at.getZ(), NamedTextColor.RED)
                    )
            );
        }
    }

    @Override
    public Component getBossBarName(LivingEntity entity) {
        return TranslationManager.translation("feature.dailyevents.bloody_night.vampire_boss.name");
    }

    @Override
    public BossBar.Color getBossBarColor() {
        return BossBar.Color.RED;
    }

    @Override
    public BossBar.Overlay getBossBarOverlay() {
        return BossBar.Overlay.NOTCHED_6;
    }

    @Override
    public double getBossBarViewRadius() {
        return 60;
    }
}

