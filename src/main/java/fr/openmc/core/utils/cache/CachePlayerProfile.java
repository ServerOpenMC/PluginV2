package fr.openmc.core.utils.cache;

import com.destroystokyo.paper.profile.PlayerProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;

import java.util.UUID;

public class CachePlayerProfile {
    private static final Object2ObjectMap<UUID, PlayerProfile> playerProfileCache = new Object2ObjectOpenHashMap<>();

    /**
     * Donne l'PlayerProfile s'il est déjà mis en cache, sinon il exécute la méthode basique
     */
    public static PlayerProfile getPlayerProfile(UUID uuid) {
        return playerProfileCache.computeIfAbsent(uuid, key -> Bukkit.createProfile(uuid));
    }
}
