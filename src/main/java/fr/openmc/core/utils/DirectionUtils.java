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

        double playerYaw = (player.getLocation().getYaw() % 360 + 360) % 360;
        double targetYaw = Math.toDegrees(Math.atan2(dx, dz));
        if (targetYaw < 0) {
            targetYaw += 360;
        }

        double deltaYaw = (targetYaw - playerYaw + 360) % 360;
        final String[] ARROWS = {
                "↑", // 0° +/- 22.5°
                "↗", // 45°
                "→", // 90°
                "↘", // 135°
                "↓", // 180°
                "↙", // 225°
                "←", // 270°
                "↖"  // 315°
        };

        // On ajoute 22.5 pour centrer les secteurs, puis on divise par 45°
        int index = (int) ((deltaYaw + 22.5) / 45) % 8;
        return ARROWS[index];
    }
}
