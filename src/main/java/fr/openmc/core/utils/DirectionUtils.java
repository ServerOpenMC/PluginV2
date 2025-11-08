package fr.openmc.core.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DirectionUtils {
    /**
     * Retourne une flèche directionnelle (↑, ↗, →, etc.) indiquant la direction de point1 à point2.
     *
     * @param player Le joueur
     * @param target Position cible (par exemple, la mascotte)
     * @return Emoji directionnel
     */
    public static String getDirectionArrow(Player player, Location target) {
        // On ne tient pas compte de la hauteur
        double dx = target.getX() - player.getLocation().getX();
        double dz = target.getZ() - player.getLocation().getZ();
        if (dx == 0 && dz == 0) {
            return "•";
        }

        double len = Math.sqrt(dx * dx + dz * dz);
        double dirX = dx / len;
        double dirZ = dz / len;

        double playerYaw = player.getLocation().getYaw();
        double yawRad = Math.toRadians(playerYaw);
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);

        double det = forwardX * dirZ - forwardZ * dirX;
        double dot = forwardX * dirX + forwardZ * dirZ;
        double angleDeg = Math.toDegrees(Math.atan2(det, dot)); // dans (-180, 180]
        if (angleDeg < 0) angleDeg += 360; // normaliser dans [0,360)

        final String[] ARROWS = {
                "↑", // 0° +/- 22.5° (devant)
                "↗", // 45°
                "→", // 90°
                "↘", // 135°
                "↓", // 180°
                "↙", // 225°
                "←", // 270°
                "↖"  // 315°
        };

        // On ajoute 22.5 pour centrer les secteurs, puis on divise par 45°
        int index = (int) ((angleDeg + 22.5) / 45) % 8;
        return ARROWS[index];
    }
}
