package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.attacks.BloodExplosionAttack;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.attacks.VampireBatAttack;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks.SummoningEffectTask;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks.VampireAttackTask;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobAttribute;
import fr.openmc.core.registry.mobs.MobAttack;
import fr.openmc.core.registry.mobs.options.MobBossbarImpl;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.LocationUtils;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks.SummoningEffectTask.SUMMON_EFFECT_INTERVAL_TICKS;

//todo: attack (blood explosion, explosive bat, mini vempire defender)
//todo: death (sfx, rewards mailbox en fonction des dégats mis (plus de dégat plus de chance)
@SuppressWarnings("UnstableApiUsage")
@Getter
public class VampireBoss extends CustomMob<Mannequin> implements MobBossbarImpl, Listener {
    private final Random random = ThreadLocalRandom.current();
    private final List<MobAttack> attacks;
    private Mannequin mannequin;

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

        this.attacks = new ArrayList<>(List.of(
                new BloodExplosionAttack(this),
                new VampireBatAttack(this)
        ));
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


        new VampireAttackTask(this).runTaskTimer(
                OMCPlugin.getInstance(),
                20L,
                20L
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

        this.mannequin = mannequin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onVampireDamageByPlayer(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();

        CustomMob<?> mob = OMCRegistry.CUSTOM_MOBS.getMob(victim);
        if (mob == null) return;
        if (!mob.getId().equals(this.getId())) return;

        Entity damager = event.getDamager();

        if (!(damager instanceof Player player)) {
            event.setCancelled(true);
            return;
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.25) {
            OMCRegistry.CUSTOM_MOBS.VAMPIRE_SLAVE.spawn(
                    LocationUtils.randomLocation(player.getLocation(), 3.0));
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

    public void pickRandomAttack() {
        if (attacks.isEmpty()) {
            return;
        }

        attacks.get(random.nextInt(attacks.size())).execute();
    }
}

