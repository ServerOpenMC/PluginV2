package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.utils.ParticleUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;

public class Breezy extends DreamMob implements Listener {

    private static final NamespacedKey BREEZY_WIND_CHARGE_KEY = new NamespacedKey(OMCPlugin.getInstance(), "breezy_wind_charge");
    public Breezy() {
        super("brezzy",
                "Breezy",
                EntityType.BREEZE,
                100.0,
                5L,
                0.7,
                4.0,
                List.of()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public EntitySnapshot createSnapshot() {
        World world = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (world == null) return null;
        Breeze breeze = world.createEntity(new Location(world, 0, 0, 0), Breeze.class);

        applyStats(breeze);

        breeze.setGlowing(true);
        breeze.setLootTable(null);

        return breeze.createSnapshot();
    }

    public static void applyIA(Breeze breeze) {
        breeze.addPotionEffect(new PotionEffect(
                PotionEffectType.INFESTED,
                Integer.MAX_VALUE,
                0,
                false,
                true
        ));

        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!breeze.isValid() || breeze.isDead()) return;

            breeze.getNearbyEntities(15, 15, 15).stream()
                    .filter(e -> e instanceof Player)
                    .map(e -> (Player) e)
                    .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(breeze.getLocation())))
                    .ifPresent(target -> shootWindCharge(breeze, target));

        }, 40L, 70L);
    }

    private static void shootWindCharge(Breeze breeze, Player target) {
        Location eye = breeze.getEyeLocation();
        Vector direction = target.getLocation().toVector().subtract(eye.toVector()).normalize();

        WindCharge charge = breeze.launchProjectile(WindCharge.class);
        charge.setVelocity(direction.multiply(1.2));
        charge.setGlowing(true);

        charge.getPersistentDataContainer().set(BREEZY_WIND_CHARGE_KEY, PersistentDataType.BYTE, (byte) 1);

        ParticleUtils.sendParticlePacket(
                target,
                eye,
                Particle.CLOUD,
                20, 0.2, 0.2, 0.2, 0.01, null
        );
    }

    @EventHandler
    public void onProjectileCollide(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile instanceof WindCharge charge)) return;
        if (!(charge.getShooter() instanceof Breeze)) return;

        if (!charge.getPersistentDataContainer().has(BREEZY_WIND_CHARGE_KEY)) return;

        Location loc = charge.getLocation();
        World world = loc.getWorld();

        world.playSound(loc, Sound.ENTITY_BREEZE_WIND_BURST, 1f, 0.8f);

        for (Entity e : world.getNearbyEntities(loc, 1.5, 1.5, 1.5)) {
            if (e instanceof Player p) {
                DreamUtils.removeDreamTime(p, this.getDamageTime(), true);
                p.setVelocity(p.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.2).setY(0.6));
            }
        }

        charge.remove();
    }
}