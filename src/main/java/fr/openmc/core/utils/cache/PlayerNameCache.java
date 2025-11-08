package fr.openmc.core.utils.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerNameCache {
    private static final Object2ObjectMap<UUID, String> nameCache = new Object2ObjectOpenHashMap<>();

    public static String getName(UUID uuid) {
        return nameCache.computeIfAbsent(uuid, id -> {
            OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer((UUID) id);
            return player.getName() != null ? player.getName() : "Inconnu";
        });
    }
}
