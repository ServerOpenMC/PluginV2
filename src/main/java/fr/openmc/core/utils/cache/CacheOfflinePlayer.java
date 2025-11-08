package fr.openmc.core.utils.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CacheOfflinePlayer {
    private static final Object2ObjectMap<UUID, OfflinePlayer> offlinePlayerCache = new Object2ObjectOpenHashMap<>();

    /**
     * Donne l'OfflinePlayer s'il est déjà mis en cache, sinon il exécute la méthode basique
     */
    public static OfflinePlayer getOfflinePlayer(UUID uuid) {
        return offlinePlayerCache.computeIfAbsent(uuid, key -> Bukkit.getOfflinePlayer((UUID) key));
    }
}
