package fr.openmc.core.utils.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldUtils {

    public static Yaw getYaw(Player p) {
        float yaw = p.getLocation().getYaw();
        yaw = (yaw % 360 + 360) % 360; // true modulo, as javas modulo is unique for negative values
        if (yaw > 135 || yaw < -135) {
            return Yaw.NORTH;
        } else if (yaw < -45) {
            return Yaw.EAST;
        } else if (yaw > 45) {
            return Yaw.WEST;
        } else {
            return Yaw.SOUTH;
        }
    }

	/**
	 * Get the center of the chunk at Y coordinates
	 *
	 * @param chunk the chunk
	 * @param y the Y location
	 */
	public static Location getChunkCenter(Chunk chunk, double y) {
		double x = 16 * chunk.getX() + 8;
		double z = 16 * chunk.getZ() + 8;
		return new Location(chunk.getWorld(), x, y, z);
	}
}
