package fr.openmc.core.features.settings;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;
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

        if (!settings.<Boolean>getSetting(SettingType.FRIEND_REQUESTS_ENABLED)) {
            return false;
        }

        return settings.canReceiveFriendRequest(senderUUID);
    }

    public boolean canReceiveCityJoinRequest(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);

        if (!settings.<Boolean>getSetting(SettingType.CITY_INVITATIONS_ENABLED)) {
            return false;
        }

        return settings.canReceiveCityJoinRequest(senderUUID);
    }

    public boolean canSeePlayerMoney(UUID viewerUUID, UUID targetUUID) {
        if (viewerUUID.equals(targetUUID)) return true;

        PlayerSettings targetSettings = getPlayerSettings(targetUUID);

        if (targetSettings.isVisibleToEveryone(SettingType.FRIEND_VISIBILITY_MONEY)) {
            return true;
        }

        return targetSettings.isVisibleToFriend(SettingType.FRIEND_VISIBILITY_MONEY, viewerUUID);
    }

    public boolean canSeePlayerCity(UUID viewerUUID, UUID targetUUID) {
        if (viewerUUID.equals(targetUUID)) return true;

        PlayerSettings targetSettings = getPlayerSettings(targetUUID);

        if (targetSettings.isVisibleToEveryone(SettingType.FRIEND_VISIBILITY_CITY)) {
            return true;
        }

        return targetSettings.isVisibleToFriend(SettingType.FRIEND_VISIBILITY_CITY, viewerUUID);
    }

    public boolean canSeePlayerStatus(UUID viewerUUID, UUID targetUUID) {
        if (viewerUUID.equals(targetUUID)) return true;

        PlayerSettings targetSettings = getPlayerSettings(targetUUID);

        if (targetSettings.isVisibleToEveryone(SettingType.FRIEND_VISIBILITY_STATUS)) {
            return true;
        }

        return targetSettings.isVisibleToFriend(SettingType.FRIEND_VISIBILITY_STATUS, viewerUUID);
    }

    public boolean canSeePlayerPlaytime(UUID viewerUUID, UUID targetUUID) {
        if (viewerUUID.equals(targetUUID)) return true;

        PlayerSettings targetSettings = getPlayerSettings(targetUUID);

        if (targetSettings.isVisibleToEveryone(SettingType.FRIEND_VISIBILITY_PLAYTIME)) {
            return true;
        }

        return targetSettings.isVisibleToFriend(SettingType.FRIEND_VISIBILITY_PLAYTIME, viewerUUID);
    }

    public boolean canReceivePrivateMessage(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.getSetting(SettingType.PRIVATE_MESSAGES_ENABLED);
    }

    public boolean shouldPlayNotificationSound(UUID playerUUID) {
        PlayerSettings settings = getPlayerSettings(playerUUID);
        return settings.getSetting(SettingType.NOTIFICATIONS_SOUND);
    }

    public boolean shouldShowFriendConnectionMessages(UUID playerUUID) {
        PlayerSettings settings = getPlayerSettings(playerUUID);
        return settings.getSetting(SettingType.FRIEND_CONNECTION_MESSAGES);
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