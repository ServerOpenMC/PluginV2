package fr.openmc.core.utils;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerNameCache {
    private static final Map<UUID, String> nameCache = new HashMap<>();

    public static String getName(UUID uuid) {
        return nameCache.computeIfAbsent(uuid, id -> {
            OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(id);
            return player.getName() != null ? player.getName() : "Inconnu";
        });
    }
}
