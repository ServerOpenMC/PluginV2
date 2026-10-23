package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.registry.regions.preset.SquareRegion;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.world.LocationUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@Getter
public class Floor {

    private final SquareRegion region;
    private final List<CustomMobEntry> entities;
    private final int entityToSpawn;
    private final double radius;

    @Setter
    public boolean canSpawn;

    public Floor(SquareRegion region, List<CustomMobEntry> entities, double radius, int entityToSpawn) {
        this(region, entities, entityToSpawn, radius, false);
    }

    public Floor(SquareRegion region, List<CustomMobEntry> entities, int entityToSpawn, double radius, boolean canSpawn) {
        this.region = region;
        this.entities = entities;
        this.entityToSpawn = entityToSpawn;
        this.radius = radius;
        this.canSpawn = canSpawn;
    }

    public void reloadMobSpawn() {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            canSpawn = true;
        },RandomUtils.randomBetween(60, 120) * 20L);
    }

    public void spawnMob() {

        canSpawn = false;

        long time = 0;

        spawn();

        for (int n = 0; n < entityToSpawn - 1; n++) {
            time += RandomUtils.randomBetween(2, 7) + n;
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::spawn, time * 20L);
            TerrorHouseManager.entityCount++;

            if (TerrorHouseManager.limitPassed())
                break;
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::reloadMobSpawn, time * 20L);
    }

    private void spawn() {

        Location spawnLoc = getRandomPosFromPlayer(
                getRandomPlayerInRegion()
        );

        new BukkitRunnable() {
            private int maxPoints = 30; //gives the amount of time the mob takes to spawn, there it's 30*2 = 60 ticks = 3 seconds
            private int points = 1;

            @Override
            public void run() {
                spawnCircle(spawnLoc, 0.8, points);

                points++;

                if (points >= maxPoints) {
                    cancel();

                    CustomMobEntry selectEntity = RandomUtils.getRandomObjectFromList(entities);

                   selectEntity.spawn(spawnLoc);
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
    }

    public Player getRandomPlayerInRegion() {
        return RandomUtils.getRandomObjectFromList(
                OMCPlugin.getInstance().getServer()
                        .getOnlinePlayers()
                        .stream()
                        .filter(p -> region.contains(p.getLocation()))
                        .toList()
        );
    }

    public Location getRandomPosFromPlayer(Player player) {
        return LocationUtils.randomLocation(
                new Location(
                        player.getWorld(),
                        player.getX(),
                        getFloorPos(player),
                        player.getZ()
                ),
                radius
        );
    }

    public int getFloorPos(Player player) {
        Block block = player.getLocation().getBlock();
        if (player.isFlying() || player.isFlying()) {
            int minY = region.getMin().y();

            while (block.getY() >= minY && block.getType().isAir())
                block = block.getRelative(0, -1, 0);
        }
        return block.getY() + 1;
    }

    public static void spawnCircle(Location center, double radius, int points) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;

            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location particleLocation = new Location(
                    world,
                    x,
                    center.getY() + 0.05,
                    z
            );

            world.spawnParticle(
                    Particle.DUST,
                    particleLocation,
                    1,
                    0, 0, 0,
                    0,
                    new Particle.DustOptions(Color.RED, 1.0f)
            );
        }
    }
}
