package fr.openmc.core.utils.cache;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.openmc.core.utils.bukkit.SkullUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachePlayerProfile {
    private static final Object2ObjectMap<UUID, PlayerProfile> playerProfileCache = new Object2ObjectOpenHashMap<>();
    private static final Set<UUID> requestedProfiles = ConcurrentHashMap.newKeySet();

    public static void setPlayerProfile(UUID uuid, PlayerProfile profile) {
        if (uuid == null || profile == null) return;
        playerProfileCache.put(uuid, profile);
    }

    /**
     * Donne le PlayerProfile s'il est déjà mis en cache, sinon il exécute la méthode basique
     */
    public static PlayerProfile getPlayerProfile(UUID uuid) {
        if (uuid == null) return null;

        PlayerProfile profile = playerProfileCache.get(uuid);
        if (profile != null
                && (profile.hasTextures()
                || !Bukkit.getOnlineMode()
                || uuid.version() != 4)) {
            return profile;
        }

        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            PlayerProfile onlineProfile = onlinePlayer.getPlayerProfile();
            playerProfileCache.put(uuid, onlineProfile);
            return onlineProfile;
        }

        if (profile != null) return profile;

        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
        String name = (offlinePlayer != null) ? offlinePlayer.getName() : null;
        PlayerProfile newProfile = (name != null) ? Bukkit.createProfile(uuid, name) : Bukkit.createProfile(uuid);
        playerProfileCache.put(uuid, newProfile);

        if (Bukkit.getOnlineMode()
                && uuid.version() == 4
                && !newProfile.hasTextures()
                && requestedProfiles.add(uuid)) {
            newProfile.update().thenAccept(updated -> {
                if (updated != null && updated.hasTextures()) {
                    playerProfileCache.put(uuid, updated);
                    SkullUtils.updatePlayerHeadCache(uuid, updated);
                }
            }).exceptionally(_ -> null)
                    .whenComplete((_, _) -> requestedProfiles.remove(uuid));;
        }

        return newProfile;
    }
}
