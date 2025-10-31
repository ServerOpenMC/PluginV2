package fr.openmc.core.features.dream;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DreamUtils {
    public static boolean isInDreamWorld(Player player) {
        return isDreamWorld(player.getLocation());
    }

    public static boolean isDreamWorld(Location loc) {
        return isDreamWorld(loc.getWorld());
    }

    public static boolean isDreamWorld(World world) {
        return world.getName().equals(DreamDimensionManager.DIMENSION_NAME);
    }

    public static boolean isInDream(Player player) {
        if (!isInDreamWorld(player)) return false;

        return DreamManager.getDreamPlayer(player) != null;
    }
}
