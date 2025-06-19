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

    /**
     * Private constructor to prevent instantiation.
     * Use getInstance() to access the singleton instance.
     */
    public PlayerSettingsManager() {
        Bukkit.getPluginManager().registerEvents(this, OMCPlugin.getInstance());
//        initializeDatabase();
    }

    /**
     * Returns the singleton instance of PlayerSettingsManager.
     *
     * @return the singleton instance
     */
    public static PlayerSettingsManager getInstance() {
        if (instance == null) {
            instance = new PlayerSettingsManager();
        }
        return instance;
    }

    /**
     * Retrieves the PlayerSettings for a given player UUID.
     * If the settings do not exist, it creates a new instance.
     *
     * @param playerUUID the UUID of the player
     * @return PlayerSettings instance for the player
     */
    public PlayerSettings getPlayerSettings(UUID playerUUID) {
        return playersSettings.computeIfAbsent(playerUUID, uuid -> {
            PlayerSettings settings = new PlayerSettings(uuid);
//            settings.();
            return settings;
        });
    }

    /**
     * Retrieves the PlayerSettings for a given player.
     * If the settings do not exist, it creates a new instance.
     *
     * @param player the player
     * @return PlayerSettings instance for the player
     */
    public PlayerSettings getPlayerSettings(Player player) {
        return getPlayerSettings(player.getUniqueId());
    }

    /**
     * Loads the PlayerSettings for a given player UUID.
     * This method can be used to load settings from a database or other storage.
     *
     * @param playerUUID the UUID of the player
     */
    public void loadPlayerSettings(UUID playerUUID) {
        PlayerSettings settings = new PlayerSettings(playerUUID);
//        settings.loadFromDatabase();
        playersSettings.put(playerUUID, settings);
    }

    /**
     * Unloads the PlayerSettings for a given player UUID.
     * This method can be used to clear settings from memory.
     *
     * @param playerUUID the UUID of the player
     */
    public void unloadPlayerSettings(UUID playerUUID) {
        playersSettings.remove(playerUUID);
    }

    /**
     * Checks if a player can receive a friend request from another player.
     *
     * @param receiverUUID the UUID of the player receiving the request
     * @param senderUUID   the UUID of the player sending the request
     * @return true if the receiver can receive the request, false otherwise
     */
    public boolean canReceiveFriendRequest(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.canReceiveFriendRequest(senderUUID);
    }

    /**
     * Checks if a player can receive a notification sound.
     *
     * @param playerUUID the UUID of the player
     * @return true if the player should play notification sound, false otherwise
     */
    public boolean shouldPlayNotificationSound(UUID playerUUID) {
        PlayerSettings settings = getPlayerSettings(playerUUID);
        return settings.getSetting(SettingType.NOTIFICATIONS_SOUND);
    }

    /**
     * Checks if a player can receive a city invite from another player.
     *
     * @param receiverUUID the UUID of the player receiving the invite
     * @param senderUUID   the UUID of the player sending the invite
     * @return true if the receiver can receive the city invite, false otherwise
     */
    public boolean canReceiveCityInvite(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.canReceiveCityJoinRequest(senderUUID);
    }

    /**
     * Checks if a player can receive a mailbox message from another player.
     *
     * @param receiverUUID the UUID of the player receiving the mailbox message
     * @param senderUUID   the UUID of the player sending the mailbox message
     * @return true if the receiver can receive the mailbox message, false otherwise
     */
    public boolean canReceiveMailbox(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.canReceiveMailbox(senderUUID);
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