package fr.openmc.core.utils.bedrock;

import fr.openmc.core.hooks.BedrockHook;
import org.bukkit.entity.Player;

public class CharRemplacementUtils {
    public static String getPointChar(Player player) {
        return BedrockHook.isBedrockPlayer(player) ?
                "■" : "•";
    }
}
