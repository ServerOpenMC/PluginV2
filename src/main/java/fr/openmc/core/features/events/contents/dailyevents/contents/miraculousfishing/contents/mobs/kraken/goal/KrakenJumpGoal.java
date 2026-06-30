package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.kraken.goal;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.EnumSet;

public class KrakenJumpGoal implements Goal<Squid> {
    private final Squid kraken;
    private long lastJumpTick = 0;
    private boolean inJump = false;

    private static final long COOLDOWN = 20 * 10; // 10 secondes, en tick
    private static final double LAND_RADIUS = 20;
    private static final double LAND_DAMAGE = 30.0;

    public KrakenJumpGoal(Squid kraken) {
        this.kraken = kraken;
    }

    @Override
    public boolean shouldActivate() {
        long currentTick = kraken.getWorld().getGameTime();
        if (currentTick - lastJumpTick < COOLDOWN) return false;
        return kraken.getTarget() != null;
    }

    @Override
    public boolean shouldStayActive() {
        return inJump;
    }

    @Override
    public void start() {
        LivingEntity target = kraken.getTarget();
        if (target == null) return;

        Location from = kraken.getLocation();
        Location to = target.getLocation();

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        kraken.setVelocity(new Vector(dx, 2, dz));

        lastJumpTick = kraken.getWorld().getGameTime();
        inJump = true;
    }

    @Override
    public void tick() {
        if (!inJump) return;

        // * Fait des dégats lorsque le kraken ratterit sur le sol
        if (kraken.isOnGround() || kraken.isInWater() && kraken.getVelocity().getY() <= 0) {
            damageOnLand();
            inJump = false;
        }
    }

    private void damageOnLand() {
        Location loc = kraken.getLocation();
        for (Entity entity : kraken.getNearbyEntities(LAND_RADIUS, LAND_RADIUS, LAND_RADIUS)) {
            if (entity instanceof Player player) {
                double dist = player.getLocation().distance(loc);
                if (dist <= LAND_RADIUS) {
                    double dmg = Math.max(2.0, LAND_DAMAGE - dist/2);
                    player.damage(dmg, kraken);
                }
            }
        }

        spawnImpactParticles(loc);
        kraken.getWorld().playSound(loc, Sound.ENTITY_GENERIC_BIG_FALL, 1.0F, 0.8F);
    }

    private void spawnImpactParticles(Location center) {
        Collection<Player> receivers = center.getNearbyEntitiesByType(Player.class, 20)
                .stream()
                .toList();

        ParticleUtils.spawnCloudParticlesToAll(
                center,
                Particle.BUBBLE,
                55,
                LAND_RADIUS,
                1,
                receivers
        );

        ParticleUtils.spawnDispersingParticles(
                center,
                Particle.CLOUD,
                25,
                (int) LAND_RADIUS,
                0.1,
                null
        );
    }

    @Override
    public @NonNull GoalKey<Squid> getKey() {
        return GoalKey.of(Squid.class,
                new NamespacedKey(OMCPlugin.getInstance(), "kraken_jump"));
    }

    @Override
    public @NonNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.JUMP);
    }
}