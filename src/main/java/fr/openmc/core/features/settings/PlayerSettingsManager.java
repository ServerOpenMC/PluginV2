package fr.openmc.core.features.settings;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.settings.models.PlayerSettingEntity;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Getter
public class PlayerSettingsManager implements Listener {

    private static final Map<UUID, PlayerSettings> playersSettings = new ConcurrentHashMap<>();
    private static Dao<PlayerSettingEntity, Long> playerSettingDao;

    /**
     * Initializes the database connection and creates tables if needed.
     *
     * @param connectionSource the database connection source
     * @throws SQLException if database initialization fails
     */
    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        playerSettingDao = DaoManager.createDao(connectionSource, PlayerSettingEntity.class);
        try {
            TableUtils.createTableIfNotExists(connectionSource, PlayerSettingEntity.class);
        } catch (SQLException e) {
            if (!e.getMessage().contains("Duplicate key")) {
                throw e;
            }
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Table for PlayerSettingEntity already exists, skipping creation.");
        }
    }

    /**
     * Retrieves the PlayerSettings for a given player UUID.
     * If the settings do not exist in memory, it creates a new instance and loads from database.
     *
     * @param playerUUID the UUID of the player
     * @return PlayerSettings instance for the player
     */
    public static PlayerSettings getPlayerSettings(UUID playerUUID) {
        return playersSettings.computeIfAbsent(playerUUID, uuid -> {
            PlayerSettings settings = new PlayerSettings(uuid);
            loadPlayerSettingsFromDatabase(uuid, settings);
            return settings;
        });
    }

    /**
     * Retrieves the PlayerSettings for a given player.
     * If the settings do not exist in memory, it creates a new instance and loads from database.
     *
     * @param player the player
     * @return PlayerSettings instance for the player
     */
    public static PlayerSettings getPlayerSettings(Player player) {
        return getPlayerSettings(player.getUniqueId());
    }

    /**
     * Loads the PlayerSettings for a given player UUID from the database.
     * This method runs asynchronously to avoid blocking the main thread.
     *
     * @param playerUUID the UUID of the player
     */
    public static void loadPlayerSettings(UUID playerUUID) {
        CompletableFuture.runAsync(() -> {
            try {
                PlayerSettings settings = playersSettings.computeIfAbsent(playerUUID, PlayerSettings::new);
                loadPlayerSettingsFromDatabase(playerUUID, settings);
            } catch (Exception e) {
                OMCPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "Failed to load player settings for " + playerUUID, e);
            }
        });
    }

    /**
     * Loads all player settings from the database and populates the playersSettings map.
     * This method runs asynchronously to avoid blocking the main thread.
     */
    public static void loadAllPlayerSettings() {
       CompletableFuture.runAsync(() -> {
            try {
                List<PlayerSettingEntity> entities = playerSettingDao.queryForAll();
                for (PlayerSettingEntity entity : entities) {
                    UUID playerUUID = UUID.fromString(entity.getPlayerUUID());
                    PlayerSettings settings = playersSettings.computeIfAbsent(playerUUID, PlayerSettings::new);
                    try {
                        SettingType settingType = entity.getSettingTypeAsEnum();
                        Object value = settingType.parseValue(entity.getSettingValue());
                        settings.setSetting(settingType, value);
                    } catch (Exception e) {
                        OMCPlugin.getInstance().getLogger().log(Level.WARNING,
                                "Failed to parse setting " + entity.getSettingType() + " for player " + playerUUID, e);
                    }
                }
            } catch (SQLException e) {
                OMCPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "Failed to load all player settings from database", e);
            }
        });
    }

    /**
     * Loads player settings from the database and applies them to the PlayerSettings instance.
     *
     * @param playerUUID the UUID of the player
     * @param settings the PlayerSettings instance to load data into
     */
    public static void loadPlayerSettingsFromDatabase(UUID playerUUID, PlayerSettings settings) {
        if (playerSettingDao == null) {
            OMCPlugin.getInstance().getLogger().warning("Player settings DAO is not initialized");
            return;
        }

        try {
            QueryBuilder<PlayerSettingEntity, Long> queryBuilder = playerSettingDao.queryBuilder();
            queryBuilder.where().eq("playerUUID", playerUUID.toString());
            PreparedQuery<PlayerSettingEntity> preparedQuery = queryBuilder.prepare();
            List<PlayerSettingEntity> entities = playerSettingDao.query(preparedQuery);

            for (PlayerSettingEntity entity : entities) {
                try {
                    SettingType settingType = entity.getSettingTypeAsEnum();
                    Object value = settingType.parseValue(entity.getSettingValue());
                    settings.setSetting(settingType, value);
                } catch (Exception e) {
                    OMCPlugin.getInstance().getLogger().log(Level.WARNING,
                            "Failed to parse setting " + entity.getSettingType() + " for player " + playerUUID, e);
                }
            }
        } catch (SQLException e) {
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Failed to load settings from database for player " + playerUUID, e);
        }
    }

    /**
     * Saves a specific setting to the database.
     *
     * @param playerUUID the UUID of the player
     * @param settingType the type of setting to save
     * @param value the value to save
     */
    public static void saveSetting(UUID playerUUID, SettingType settingType, Object value) {
        if (playerSettingDao == null) {
            OMCPlugin.getInstance().getLogger().warning("Player settings DAO is not initialized");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                QueryBuilder<PlayerSettingEntity, Long> queryBuilder = playerSettingDao.queryBuilder();
                queryBuilder.where()
                        .eq("playerUUID", playerUUID.toString())
                        .and()
                        .eq("settingType", settingType.name());
                PreparedQuery<PlayerSettingEntity> preparedQuery = queryBuilder.prepare();
                PlayerSettingEntity existingEntity = playerSettingDao.queryForFirst(preparedQuery);

                if (existingEntity != null) {
                    existingEntity.updateValue(value);
                    playerSettingDao.update(existingEntity);
                } else {
                    PlayerSettingEntity newEntity = new PlayerSettingEntity(playerUUID, settingType, value);
                    playerSettingDao.create(newEntity);
                }
            } catch (SQLException e) {
                OMCPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "Failed to save setting " + settingType + " for player " + playerUUID, e);
            }
        });
    }

    /**
     * Saves all settings for a player to the database.
     *
     * @param playerUUID the UUID of the player
     */
    public static void saveAllPlayerSettings(UUID playerUUID) {
        PlayerSettings settings = playersSettings.remove(playerUUID);
        if (settings == null) return;

        for (SettingType settingType : SettingType.values()) {
            Object value = settings.getSetting(settingType);
            saveSetting(playerUUID, settingType, value);
        }
    }

    /**
     * Unloads the PlayerSettings for a given player UUID and saves them to the database.
     *
     * @param playerUUID the UUID of the player
     */
    public static void unloadPlayerSettings(UUID playerUUID) {
        PlayerSettings settings = playersSettings.get(playerUUID);
        if (settings != null)
            saveAllPlayerSettings(playerUUID);
    }

    /**
     * Saves all player settings to the database.
     * This method should be called during server shutdown.
     */
    public static void saveAllSettings() {
        for (UUID playerUUID : playersSettings.keySet()) {
            saveAllPlayerSettings(playerUUID);
        }
    }

    /**
     * Checks if a player can receive a friend request from another player.
     *
     * @param receiverUUID the UUID of the player receiving the request
     * @param senderUUID   the UUID of the player sending the request
     * @return true if the receiver can receive the request, false otherwise
     */
    public static boolean canReceiveFriendRequest(UUID receiverUUID, UUID senderUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.canPerformAction(SettingType.FRIEND_REQUESTS_POLICY, senderUUID);
    }

    /**
     * Checks if a player can receive a notification sound.
     *
     * @param playerUUID the UUID of the player
     * @return true if the player should play notification sound, false otherwise
     */
    public static boolean shouldPlayNotificationSound(UUID playerUUID) {
        PlayerSettings settings = getPlayerSettings(playerUUID);
        return settings.getSetting(SettingType.NOTIFICATIONS_SOUND);
    }

    /**
     * Checks if a player can receive a city invite from another player.
     *
     * @param senderUUID     the UUID of the player send the invite
     * @param receiverUUID   the UUID of the player receive the invite
     * @return true if the receiver can receive the city invite, false otherwise
     */
    public static boolean canReceiveCityInvite(UUID senderUUID, UUID receiverUUID) {
        PlayerSettings settings = getPlayerSettings(receiverUUID);
        return settings.canPerformAction(SettingType.CITY_JOIN_REQUESTS_POLICY, senderUUID);
    }

    // ============== Event Handlers ==============
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerSettings(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        System.out.println(playerSettingDao);
        unloadPlayerSettings(uuid);
    }
}