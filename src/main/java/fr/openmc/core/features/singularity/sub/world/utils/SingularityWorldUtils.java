package fr.openmc.core.features.singularity.sub.world.utils;

import fr.openmc.core.features.singularity.sub.world.SingularityWorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SingularityWorldUtils {

    public static boolean isSingularityWorld(Location location) {
        return isSingularityWorld(location.getWorld());
    }

    public static boolean isSingularityWorld(World world) {
        return world.getName().equals(SingularityWorldManager.SINGULARITY_WORLD_NAME);
    }

    public static boolean isInSingularityWorld(Player player) {
        return isSingularityWorld(player.getWorld());
    }
}
