package fr.openmc.core.features.settings;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerSettingsManager implements Listener {

    private static PlayerSettingsManager instance;
    private final Map<UUID, PlayerSettings> playersSettings = new HashMap<>();

    private PlayerSettingsManager() {
        Bukkit.getPluginManager().registerEvents(this, OMCPlugin.getInstance());
//        initializeDatabase();
    }

    public static PlayerSettingsManager getInstance() {
        if (instance == null) {
            instance = new PlayerSettingsManager();
        }
        return instance;
    }

    public PlayerSettings getPlayerSettings(UUID playerUUID) {
        return playersSettings.computeIfAbsent(playerUUID, uuid -> {
            PlayerSettings settings = new PlayerSettings(uuid);
//            settings.();
            return settings;
        });
    }

    public PlayerSettings getPlayerSettings(Player player) {
        return getPlayerSettings(player.getUniqueId());
    }

    public void loadPlayerSettings(UUID playerUUID) {
        PlayerSettings settings = new PlayerSettings(playerUUID);
//        settings.loadFromDatabase();
        playersSettings.put(playerUUID, settings);
    }

    public void unloadPlayerSettings(UUID playerUUID) {
        playersSettings.remove(playerUUID);
    }

    public boolean canReceiveFriendRequest(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.canReceiveFriendRequest(senderUUID);
    }

    public boolean shouldPlayNotificationSound(UUID playerUUID) {
        PlayerSettings settings = getPlayerSettings(playerUUID);
        return settings.getSetting(SettingType.NOTIFICATIONS_SOUND);
    }

    // ============== Event Handlers ==============
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerSettings(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
//         unloadPlayerSettings(event.getPlayer().getUniqueId());
    }
}