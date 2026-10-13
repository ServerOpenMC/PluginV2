package fr.openmc.core.features.settings;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import fr.openmc.core.features.settings.command.SettingsCommand;
import fr.openmc.core.features.settings.listeners.PlayerSettingsListener;
import fr.openmc.core.features.settings.models.DBPlayerSetting;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Credit(developers = {"Axeno"}, graphist = {"Gexary"})
@Getter
public class PlayerSettingsManager extends Feature implements HasDatabase, HasListeners, HasCommands {
    private final Map<UUID, PlayerSettings> playersSettings = new ConcurrentHashMap<>();
    private final DBTable<DBPlayerSetting, Long> playerSettingTable = DBTable.of(DBPlayerSetting.class);

    @Override
    public void onEnable() {
        this.loadAllPlayerSettings();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new SettingsCommand()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(PlayerSettingsListener::new);
    }

    @Override
    public void onDisable() {
        this.saveAllSettings();
    }

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(playerSettingTable);
    }

    /**
     * Retrieves the PlayerSettings for a given player UUID.
     * If the settings do not exist in memory, it creates a new instance and loads from database.
     *
     * @param playerUUID the UUID of the player
     * @return PlayerSettings instance for the player
     */
    public PlayerSettings getPlayerSettings(UUID playerUUID) {
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
    public PlayerSettings getPlayerSettings(Player player) {
        return getPlayerSettings(player.getUniqueId());
    }

    /**
     * Loads the PlayerSettings for a given player UUID from the database.
     * This method runs asynchronously to avoid blocking the main thread.
     *
     * @param playerUUID the UUID of the player
     */
    public void loadPlayerSettings(UUID playerUUID) {
        CompletableFuture.runAsync(() -> {
            try {
                PlayerSettings settings = playersSettings.computeIfAbsent(playerUUID, PlayerSettings::new);
                loadPlayerSettingsFromDatabase(playerUUID, settings);
            } catch (Exception e) {
                OMCLogger.error("Failed to load player settings for UUID: {}", playerUUID, e);
            }
        });
    }

    /**
     * Loads all player settings from the database and populates the playersSettings map.
     * This method runs asynchronously to avoid blocking the main thread.
     */
    public void loadAllPlayerSettings() {
        CompletableFuture.runAsync(() -> {
            List<DBPlayerSetting> entities = playerSettingTable.loadList();
            for (DBPlayerSetting entity : entities) {
                UUID playerUUID = UUID.fromString(entity.getPlayerUUID());
                PlayerSettings settings = playersSettings.computeIfAbsent(playerUUID, PlayerSettings::new);
                try {
                    SettingType settingType = entity.getSettingTypeAsEnum();
                    Object value = settingType.parseValue(entity.getSettingValue());
                    settings.setSetting(settingType, value);
                } catch (Exception e) {
                    OMCLogger.warn("Failed to parse setting {} for player {}", entity.getSettingType(), playerUUID, e);
                }
            }
        });
    }

    /**
     * Loads player settings from the database and applies them to the PlayerSettings instance.
     *
     * @param playerUUID the UUID of the player
     * @param settings   the PlayerSettings instance to load data into
     */
    public void loadPlayerSettingsFromDatabase(UUID playerUUID, PlayerSettings settings) {
        if (playerSettingTable == null) {
            OMCLogger.warn("Player settings DAO is not initialized", new Exception());
            return;
        }

        try {
            QueryBuilder<DBPlayerSetting, Long> queryBuilder = playerSettingTable.queryBuilder();
            queryBuilder.where().eq("playerUUID", playerUUID.toString());
            PreparedQuery<DBPlayerSetting> preparedQuery = queryBuilder.prepare();
            List<DBPlayerSetting> entities = playerSettingTable.query(preparedQuery);

            for (DBPlayerSetting entity : entities) {
                try {
                    SettingType settingType = entity.getSettingTypeAsEnum();
                    Object value = settingType.parseValue(entity.getSettingValue());
                    settings.setSetting(settingType, value);
                } catch (Exception e) {
                    OMCLogger.warn("Failed to parse setting {} for player {}", entity.getSettingType(), playerUUID, e);
                }
            }
        } catch (SQLException e) {
            OMCLogger.error("Failed to load settings from database for player {}", playerUUID, e);
        }
    }

    /**
     * Saves a specific setting to the database.
     *
     * @param playerUUID  the UUID of the player
     * @param settingType the type of setting to save
     * @param value       the value to save
     */
    public void saveSetting(UUID playerUUID, SettingType settingType, Object value) {
        if (playerSettingTable == null) {
            OMCLogger.warn("Player settings DAO is not initialized", new Exception());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                QueryBuilder<DBPlayerSetting, Long> queryBuilder = playerSettingTable.queryBuilder();
                queryBuilder.where()
                        .eq("playerUUID", playerUUID.toString())
                        .and()
                        .eq("settingType", settingType.name());
                PreparedQuery<DBPlayerSetting> preparedQuery = queryBuilder.prepare();
                DBPlayerSetting existingEntity = playerSettingTable.queryForFirst(preparedQuery);

                if (existingEntity != null) {
                    existingEntity.updateValue(value);
                    playerSettingTable.update(existingEntity);
                } else {
                    DBPlayerSetting newEntity = new DBPlayerSetting(playerUUID, settingType, value);
                    playerSettingTable.create(newEntity);
                }
            } catch (SQLException e) {
                OMCLogger.error("Failed to save setting {} for player {}", settingType, playerUUID, e);
            }
        });
    }

    /**
     * Saves all settings for a player to the database.
     *
     * @param playerUUID the UUID of the player
     */
    public void saveAllPlayerSettings(UUID playerUUID) {
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
    public void unloadPlayerSettings(UUID playerUUID) {
        PlayerSettings settings = playersSettings.get(playerUUID);
        if (settings != null)
            saveAllPlayerSettings(playerUUID);
    }

    /**
     * Saves all player settings to the database.
     * This method should be called during server shutdown.
     */
    public void saveAllSettings() {
        for (UUID playerUUID : playersSettings.keySet()) {
            saveAllPlayerSettings(playerUUID);
        }
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
}