package fr.openmc.core.features.events.contents.christmas.mecanism.dimension;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChristmasWorldUtils {
    public static boolean isInChristmasWorld(Player player) {
        return isChristmasWorld(player.getLocation());
    }

    public static boolean isChristmasWorld(Location loc) {
        return isChristmasWorld(loc.getWorld());
    }

    public static boolean isChristmasWorld(World world) {
        return world.getName().equals(ChristmasDimensionManager.DIMENSION_NAME);
    }

}
