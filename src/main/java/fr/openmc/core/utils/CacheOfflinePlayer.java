package fr.openmc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheOfflinePlayer {
    private static final Map<UUID, OfflinePlayer> offlinePlayerCache = new HashMap<>();

    /**
     * Donne l'OfflinePlayer si il est déjà mis en cache, sinon il execute la méthode basique
     */
    public static OfflinePlayer getOfflinePlayer(UUID uuid) {
         return offlinePlayerCache.computeIfAbsent(uuid, Bukkit::getOfflinePlayer);
    }
}
