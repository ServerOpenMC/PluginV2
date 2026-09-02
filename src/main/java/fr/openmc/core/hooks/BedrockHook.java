package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;
import org.bukkit.entity.Player;
//import org.geysermc.floodgate.api.FloodgateApi;
//import org.geysermc.geyser.api.GeyserApi;

import java.util.Set;

public class BedrockHook extends Hooks {
    public static boolean isEnabled() {
        return false; // return Hooks.isEnabled(FancyNpcsHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Set.of("Geyser-Spigot", "floodgate", "RiftEngine");
    }

//    @Getter
//    private static GeyserApi geyserApi;
//    @Getter
//    private static FloodgateApi floodgateApi;

    @Override
    public void init() {
//        floodgateApi = FloodgateApi.getInstance();
//        geyserApi = GeyserApi.api();
    }

    /**
     * Vérifie si le joueur est un joueur Bedrock
     * @param player le joueur EN LIGNE
     * @return true si le joueur est un joueur Bedrock, false sinon
     */
    public static boolean isBedrockPlayer(Player player) {
        if (!isEnabled()) return false;
        return false;
//        return floodgateApi.isFloodgatePlayer(player.getUniqueId());
    }

    public static Character getGlyph(String namespacedId) {
//        Glyph glyph = RiftRegistry.GLYPHS.get(namespacedId).orElse(null);
//        if (glyph == null) return null;
//
//        return glyph.getBedrockChar();
        return null;
    }
}
