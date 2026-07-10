package fr.openmc.core.utils.world;

import fr.openmc.core.features.dream.DreamDimensionManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldUtils {

    /**
     * Renvoie la translation key de l'affichage du nom d'un monde
     * @param worldId l'id du monde
     * @return la translation key lié au nom de la dimension
     */
    public static String getDisplayedWorldName(String worldId) {
        switch (worldId) {
            case "world" -> {
                return "utils.world.overworld";
            }
            case "world_nether" -> {
                return "utils.world.nether";
            }
            case "world_the_end" -> {
                return "utils.world.end";
            }
            case DreamDimensionManager.DIMENSION_NAME -> {
                return "utils.world.dream";
            }
            default -> {
                return worldId;
            }
        }
    }

    public static Yaw getYaw(Player p) {
        float yaw = p.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        if (yaw > 135 && yaw <= 225) {
            return Yaw.NORTH;
        } else if (yaw > 225 && yaw <= 315) {
            return Yaw.EAST;
        } else if (yaw > 45 && yaw <= 135) {
            return Yaw.WEST;
        } else if (yaw > 315 || yaw <= 45) {
            return Yaw.SOUTH;
        } else {
            return Yaw.NORTH; // should never happen
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
