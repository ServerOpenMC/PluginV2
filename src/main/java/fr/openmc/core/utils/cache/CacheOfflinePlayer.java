package fr.openmc.core.utils.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CacheOfflinePlayer {
    private static final Object2ObjectMap<UUID, OfflinePlayer> offlinePlayerByUUIDCache = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<String, OfflinePlayer> offlinePlayerByNameCache = new Object2ObjectOpenHashMap<>();

    /**
     * Donne l'OfflinePlayer s'il est déjà mis en cache, sinon il exécute la méthode basique
     * Consomme plus que d'habitude, a utiliser avec modération
     */
    public static OfflinePlayer getOfflinePlayer(String playerName) {
        return offlinePlayerByNameCache.computeIfAbsent(playerName, key -> Bukkit.getOfflinePlayer(playerName));
    }

    /**
     * Donne l'OfflinePlayer s'il est déjà mis en cache, sinon il exécute la méthode basique
     */
    public static OfflinePlayer getOfflinePlayer(UUID uuid) {
        return offlinePlayerByUUIDCache.computeIfAbsent(uuid, key -> Bukkit.getOfflinePlayer((UUID) key));
    }
}
