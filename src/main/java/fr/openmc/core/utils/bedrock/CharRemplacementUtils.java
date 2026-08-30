package fr.openmc.core.utils.bedrock;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class CharRemplacementUtils {
    public static String getPointChar(Player player) {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId()) ?
                "■" : "•";
    }
}
