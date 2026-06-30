package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.kraken;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.kraken.goal.KrakenDashGoal;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs.kraken.goal.KrakenJumpGoal;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobAttribute;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class Kraken extends CustomMob<Squid> implements Listener {
    public Kraken(String id) {
        super(id,
                "Kraken",
                Squid.class,
                100,
                20,
                List.of(
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.KRAKEN_HEAD,
                                0.25, 1, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_LEGGINGS, 0.1, 1, 1),
                        new XpLoot(120, 150, 1)
                ),
                new CustomMobAttribute(Attribute.SCALE, 90)
        );
    }

    @Override
    public Squid spawn(Location spawnLocation) {
        Squid squid = this.getPreBuildMob(spawnLocation);

        Bukkit.getMobGoals().addGoal(squid, 1, new KrakenDashGoal(squid));
        Bukkit.getMobGoals().addGoal(squid, 2, new KrakenJumpGoal(squid));

        startTargetUpdate(squid);

        return squid;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        Location center = event.getEntity().getLocation().add(0, 1, 0);
        World world = center.getWorld();

        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
        world.spawnParticle(Particle.EXPLOSION, center, 1);

        int squidCount = 10;

        for (int i = 0; i < squidCount; i++) {
            Vector velocity = new Vector(
                    RandomUtils.randomBetween(-1, 1),
                    RandomUtils.randomBetween(0, 1),
                    RandomUtils.randomBetween(-1, 1));

            Squid squid = world.spawn(center, Squid.class);

            squid.setVelocity(velocity);
        }
    }

    private void startTargetUpdate(Squid kraken) {
        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), task -> {
            if (kraken.isDead()) {
                task.cancel();
                return;
            }

            Optional<Player> target = kraken.getLocation().getNearbyPlayers(20).stream()
                    .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(kraken.getLocation())));

            target.ifPresent(kraken::setTarget);
        }, 20L, 60L);
    }

    private final Set<EntityDamageEvent.DamageCause> IMMUNE_DAMAGE = Set.of(
            EntityDamageEvent.DamageCause.DRYOUT,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.FALL
    );

    @EventHandler
    public void onPotionEffectOnKraken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Squid kraken)) return;

        if (!CustomMobRegistry.isCustomMob(kraken)) return;
        if (!OMCRegistry.CUSTOM_MOBS.getMob(kraken).getId().equals(this.getId())) return;

        if (!IMMUNE_DAMAGE.contains(event.getCause())) return;
        event.setCancelled(true);
    }
}