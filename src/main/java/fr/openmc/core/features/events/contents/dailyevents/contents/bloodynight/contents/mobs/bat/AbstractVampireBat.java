package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bat;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobAttribute;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Comparator;

public abstract class AbstractVampireBat extends CustomMob<Bat> {

    private static final double SPEED = 0.55;
    private static final double SEARCH_RADIUS = 30.0;

    private static final int MAX_LIFETIME_TICKS = 20 * 15;

    public AbstractVampireBat(String id, String name) {
        super(
                id,
                name,
                Bat.class,
                10.0,
                0.0,
                new CustomMobAttribute(Attribute.KNOCKBACK_RESISTANCE, 1.0)
        );
    }

    public Bat spawn(Location location, Player target) {
        Bat bat = getPreBuildMob(location.clone());

        bat.setAwake(true);
        bat.setPersistent(false);

        startTargeting(bat, target);

        return bat;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Bat bat)) return;

        impactAndRemoveBat(bat, null);
    }

    // * a override
    public void onImpact(Bat bat, Player target, Location impactLocation) {}

    private void impactAndRemoveBat(Bat bat, Player target) {
        Location impactLocation = bat.getLocation().clone();

        onImpact(bat, target, impactLocation);
        removeBat(bat);
    }

    private void startTargeting(Bat bat, Player initialTarget) {
        new BukkitRunnable() {
            private Player target = initialTarget;
            private int elapsedTicks;

            @Override
            public void run() {
                if (!isBatAlive(bat)) {
                    cancel();
                    return;
                }

                elapsedTicks += 2;

                if (elapsedTicks >= MAX_LIFETIME_TICKS) {
                    removeBat(bat);
                    cancel();
                    return;
                }

                if (!isValidTarget(bat, target)) {
                    target = findNearestTarget(bat);

                    if (target == null) {
                        removeBat(bat);
                        cancel();
                        return;
                    }
                }

                Location targetLocation = target.getEyeLocation();
                Location batLocation = bat.getLocation();

                BoundingBox impactBox = target.getBoundingBox().expand(0.2);

                if (impactBox.contains(batLocation.toVector())) {
                    impactAndRemoveBat(bat, target);
                    cancel();
                    return;
                }

                Vector direction = targetLocation.toVector()
                        .subtract(batLocation.toVector());

                if (direction.lengthSquared() > 0.001) {
                    bat.setVelocity(direction.normalize().multiply(SPEED));
                }

                playTravelParticles(bat);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
    }

    public void playTravelParticles(Bat bat) {
        ParticleUtils.sendParticlePacket(
                Particle.SMOKE,
                bat.getLocation(),
                32
        );
    }

    private Player findNearestTarget(Bat bat) {
        return bat.getWorld()
                .getNearbyPlayers(bat.getLocation(), SEARCH_RADIUS)
                .stream()
                .filter(player -> isValidTarget(bat, player))
                .min(Comparator.comparingDouble(player ->
                        player.getLocation().distanceSquared(bat.getLocation())
                ))
                .orElse(null);
    }

    private boolean isValidTarget(Bat bat, Player player) {
        return player != null
                && player.isValid()
                && !player.isDead()
                && player.getWorld().equals(bat.getWorld())
                && player.getGameMode() != GameMode.CREATIVE
                && player.getGameMode() != GameMode.SPECTATOR;
    }

    private boolean isBatAlive(Bat bat) {
        return bat != null
                && bat.isValid()
                && !bat.isDead();
    }

    private void removeBat(Bat bat) {
        unregisterAsCustomMob(bat);
        bat.remove();
    }
}