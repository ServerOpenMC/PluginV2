package fr.openmc.core.utils.bukkit;

import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class VelocityUtils {
    public static void jump(LivingEntity entity, double distance) {
        Location to = LocationUtils.randomLocation(entity.getLocation(), distance);
        jumpTo(entity, to);
    }

    public static void jumpTo(LivingEntity entity, Location to) {
        jumpTo(entity, to, 0.85);
    }

    public static void jumpTo(LivingEntity entity, Location to, double vy) {
        Location from = entity.getLocation();

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        double tick = (vy / 0.08) / 2;
        double vx = dx / tick;
        double vz = dz / tick;

        entity.setVelocity(new Vector(vx, vy, vz));
    }
}
