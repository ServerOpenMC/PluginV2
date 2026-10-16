package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class FishingMiniGameManager {
    private static final Map<UUID, FishingMiniGameMenu> ACTIVE_GAMES = new HashMap<>();

    private FishingMiniGameManager() {
    }

    public static boolean start(Player player, Consumer<FishingMiniGameResult> callback) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(callback, "callback");

        UUID playerId = player.getUniqueId();
        if (ACTIVE_GAMES.containsKey(playerId)) {
            return false;
        }

        FishingMiniGameMenu menu = new FishingMiniGameMenu(player, callback);
        ACTIVE_GAMES.put(playerId, menu);

        try {
            menu.open();
            menu.start();
            return true;
        } catch (RuntimeException exception) {
            ACTIVE_GAMES.remove(playerId);
            menu.abort();
            return false;
        }
    }

    public static void remove(Player player) {
        ACTIVE_GAMES.remove(player.getUniqueId());
    }

    public static boolean isActive(Player player) {
        return ACTIVE_GAMES.containsKey(player.getUniqueId());
    }
}
