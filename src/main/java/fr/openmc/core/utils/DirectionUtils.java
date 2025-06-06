package fr.openmc.core.utils;

import org.bukkit.Location;

public class DirectionUtils {
    /**
     * Retourne une flèche directionnelle (↑, ↗, →, etc.) indiquant la direction de point1 à point2.
     *
     * @param point1 Position de départ (souvent la position du joueur)
     * @param point2 Position cible (par exemple, la mascotte)
     * @return Emoji directionnel
     */
    public static String getDirectionEmoji(Location point1, Location point2) {
        double dx = point2.getX() - point1.getX();
        double dz = point2.getZ() - point1.getZ();
        double angle = Math.toDegrees(Math.atan2(-dx, dz));

        angle = (angle + 360) % 360;

        if (angle < 22.5) return "↓";
        else if (angle < 67.5) return "↙";
        else if (angle < 112.5) return "←";
        else if (angle < 157.5) return "↖";
        else if (angle < 202.5) return "↑";
        else if (angle < 247.5) return "↗";
        else if (angle < 292.5) return "→";
        else if (angle < 337.5) return "↘";
        else return "↓";
    }
}
