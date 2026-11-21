package fr.openmc.core.utils.cache;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CachePlayerProfile {
    private static final int SPACING_TICKS = 30; // 1,5s
    private static final int MAX_RETRIES = 2;

    private static final Map<UUID, PlayerProfile> CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, List<Consumer<PlayerProfile>>> PENDING = new HashMap<>();
    private static int nextSlot = 0;

    public static PlayerProfile getCached(UUID uuid) {
        if (uuid == null) return null;

        PlayerProfile cached = CACHE.get(uuid);
        if (cached != null) return cached;

        Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.getPlayerProfile().hasTextures()) {
            cached = online.getPlayerProfile();
            CACHE.put(uuid, cached);
        }
        return cached;
    }

    public static void request(UUID uuid, Consumer<PlayerProfile> callback) {
        PlayerProfile cached = getCached(uuid);
        if (cached != null) {
            if (callback != null) callback.accept(cached);
            return;
        }

        List<Consumer<PlayerProfile>> waiting = PENDING.get(uuid);
        if (waiting != null) {
            if (callback != null) waiting.add(callback);
            return;
        }

        waiting = new ArrayList<>();
        if (callback != null) waiting.add(callback);
        PENDING.put(uuid, waiting);
        fetch(uuid, 0);
    }

    private static void fetch(UUID uuid, int attempt) {
        int now = Bukkit.getCurrentTick();
        nextSlot = Math.max(nextSlot, now) + SPACING_TICKS;

        Bukkit.getScheduler().runTaskLaterAsynchronously(OMCPlugin.getInstance(), () -> {
            PlayerProfile found = null;
            try {
                PlayerProfile profile = Bukkit.createProfile(uuid).update().join();
                if (profile.hasTextures()) found = profile;
            } catch (Exception ignored) {}

            PlayerProfile result = found;
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                if (result == null && attempt < MAX_RETRIES) {
                    fetch(uuid, attempt + 1);
                    return;
                }

                List<Consumer<PlayerProfile>> callbacks = PENDING.remove(uuid);
                if (result == null) return;

                CACHE.put(uuid, result);
                callbacks.forEach(c -> c.accept(result));
            });
        }, nextSlot - now);
    }
}