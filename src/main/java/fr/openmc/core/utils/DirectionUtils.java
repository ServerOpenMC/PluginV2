package fr.openmc.core.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DirectionUtils {
    /**
     * Retourne une flèche directionnelle (↑, ↗, →, etc.) indiquant la direction de point1 à point2.
     *
     * @param player Le joueur
     * @param target Position cible (par exemple, la mascotte)
     * @return Emoji directionnel
     */
    public static String getDirectionArrow(Player player, Location target) {
        Vector delta = target.toVector().subtract(player.getLocation().toVector());
        delta.setY(0);
        if (delta.lengthSquared() == 0) {
            return "•";
        }

        double yawRad = Math.toRadians(player.getLocation().getYaw());
        Vector forward = new Vector(-Math.sin(yawRad), 0, Math.cos(yawRad));
        delta = delta.normalize();
        double dot = forward.dot(delta);
        Vector cross = forward.clone().crossProduct(delta);
        double angle = Math.toDegrees(Math.atan2(-cross.getY(), dot));

        if (angle > 157.5 || angle <= -157.5) return "↓";
        if (angle > 112.5) return "↘";
        if (angle > 67.5) return "→";
        if (angle > 22.5) return "↗";
        if (angle > -22.5) return "↑";
        if (angle > -67.5) return "↖";
        if (angle > -112.5) return "←";
        return "↙";
    }
}
