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
        offlinePlayerCache.forEach((uuid1, offlinePlayer) -> System.out.println(uuid1 + " " + offlinePlayer.getName()));
        return offlinePlayerCache.computeIfAbsent(uuid, Bukkit::getOfflinePlayer);
    }
}
