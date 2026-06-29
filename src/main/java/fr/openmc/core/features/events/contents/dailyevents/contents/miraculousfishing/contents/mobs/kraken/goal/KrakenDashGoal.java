package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.kraken.goal;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Squid;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;

public class KrakenDashGoal implements Goal<Squid> {
    private final Squid kraken;
    private long lastDashTick = 0;
    private int dashTicks = 0;
    private Vector dashDirection;

    private static final long COOLDOWN = 20 * 5; // 5 secondes, en tick

    public KrakenDashGoal(Squid kraken) {
        this.kraken = kraken;
    }

    @Override
    public boolean shouldActivate() {
        long currentTick = kraken.getWorld().getGameTime();
        if (currentTick - lastDashTick < COOLDOWN) return false;

        LivingEntity target = kraken.getTarget();
        return target != null && kraken.isInWater();
    }

    @Override
    public boolean shouldStayActive() {
        return dashTicks > 0;
    }

    @Override
    public void start() {
        LivingEntity target = kraken.getTarget();
        if (target == null) return;

        dashDirection = target.getLocation().toVector()
                .subtract(kraken.getLocation().toVector())
                .setY(0);
        dashTicks = 25;
        lastDashTick = kraken.getWorld().getGameTime();
    }

    @Override
    public void stop() {
        dashTicks = 0;
    }

    @Override
    public void tick() {
        kraken.setVelocity(dashDirection.clone().multiply(0.2));
        dashTicks--;

        spawnInkTrail();
    }

    private void spawnInkTrail() {
        Location loc = kraken.getLocation();

        Vector behind = dashDirection.clone().multiply(-1.0);
        Location inkLoc = loc.clone().add(behind);

        ParticleUtils.spawnCubeParticles(
                inkLoc, Particle.SQUID_INK, 10, 10, 10, 100, 30, null);

        World world = loc.getWorld();
        AreaEffectCloud cloud = world.spawn(world.getHighestBlockAt(loc).getLocation(), AreaEffectCloud.class);
        cloud.setRadius(10F);
        cloud.setRadiusPerTick(-0.0035F);
        cloud.setDuration(60);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 1), true);
        cloud.setColor(Color.fromRGB(10, 10, 15));
        cloud.setSource(kraken);
    }

    @Override
    public @NonNull GoalKey<Squid> getKey() {
        return GoalKey.of(Squid.class,
                new NamespacedKey(OMCPlugin.getInstance(), "kraken_dash"));
    }

    @Override
    public @NonNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.JUMP);
    }
}
