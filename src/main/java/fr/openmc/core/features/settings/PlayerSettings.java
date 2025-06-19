package fr.openmc.core.features.settings;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.settings.policy.CityPolicy;
import fr.openmc.core.features.settings.policy.FriendPolicy;
import fr.openmc.core.features.settings.policy.GlobalPolicy;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSettings {

    @Getter private final UUID playerUUID;
    private final Map<SettingType, Object> settings = new HashMap<>();
    private boolean loaded = false;

    /**
     * Constructs a PlayerSettings instance for the given player UUID.
     * Initializes settings with default values.
     *
     * @param playerUUID the UUID of the player
     */
    public PlayerSettings(UUID playerUUID) {
        this.playerUUID = playerUUID;
        loadDefaultSettings();
    }

    /**
     * Loads default settings for the player.
     * This method is called when the settings are first accessed.
     */
    private void loadDefaultSettings() {
        for (SettingType settingType : SettingType.values()) {
            settings.put(settingType, settingType.getDefaultValue());
        }
        loaded = true;
    }

    /**
     * Retrieves the setting value for the given SettingType.
     * If settings are not loaded, it loads default settings first.
     *
     * @param settingType the type of setting to retrieve
     * @param <T>         the expected type of the setting value
     * @return the setting value, or the default value if not set
     */
    @SuppressWarnings("unchecked")
    public <T> T getSetting(SettingType settingType) {
        if (!loaded) {
            loadDefaultSettings();
            return (T) settingType.getDefaultValue();
        }
        return (T) settings.getOrDefault(settingType, settingType.getDefaultValue());
    }

    /**
     * Sets the value for the given SettingType.
     * Validates the value against the expected type before setting it.
     *
     * @param settingType the type of setting to set
     * @param value       the value to set
     * @throws IllegalArgumentException if the value is not valid for the setting type
     */
    public void setSetting(SettingType settingType, Object value) {
        try {
            if (!settingType.isValidValue(value)) {
                throw new IllegalArgumentException("Invalid value for setting: " + settingType + ". Expected type: " + settingType.getValueType() + ", but got: " + value);
            }
            settings.put(settingType, value);
        } catch (Exception e) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§cErreur: " + e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the setting value from a string representation.
     * Parses the string according to the SettingType's expected value type.
     *
     * @param settingType the type of setting to set
     * @param value       the string value to parse and set
     */
    public void setSettingFromString(SettingType settingType, String value) {
        try {
            Object parsedValue = settingType.parseValue(value);
            setSetting(settingType, parsedValue);
        } catch (Exception e) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§cErreur lors du parsing: " + e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Resets the setting to its default value.
     *
     * @param settingType the type of setting to reset
     */
    public void resetSetting(SettingType settingType) {
        setSetting(settingType, settingType.getDefaultValue());
    }

    /**
     * Resets all settings to their default values.
     * This method iterates through all SettingTypes and resets each one.
     */
    public void resetAllSettings() {
        for (SettingType settingType : SettingType.values()) {
            resetSetting(settingType);
        }
    }

    /**
     * Checks if the player can perform an action based on the given setting type and target UUID.
     * The action is determined by the policy associated with the setting type.
     *
     * @param settingType the type of setting to check
     * @param targetUUID  the UUID of the target player
     * @return true if the action can be performed, false otherwise
     */
    public boolean canPerformAction(SettingType settingType, UUID targetUUID) {
        Object policy = getSetting(settingType);

        if (policy instanceof FriendPolicy friendPolicy) {
            return switch (friendPolicy) {
                case EVERYONE -> true;
                case CITY_MEMBERS_ONLY -> areSameCityMembers(playerUUID, targetUUID);
                case NOBODY -> false;
            };
        }

        if (policy instanceof CityPolicy cityPolicy) {
            return switch (cityPolicy) {
                case EVERYONE -> true;
                case FRIENDS -> FriendManager.areFriends(playerUUID, targetUUID);
                case NOBODY -> false;
            };
        }

        if (policy instanceof GlobalPolicy globalPolicy) {
            return switch (globalPolicy) {
                case EVERYONE -> true;
                case FRIENDS -> FriendManager.areFriends(playerUUID, targetUUID);
                case CITY_MEMBERS -> areSameCityMembers(playerUUID, targetUUID);
                case NOBODY -> false;
            };
        }

        throw new IllegalArgumentException("Unsupported policy: " + policy);
    }

    /**
     * Checks if the player is visible to a friend based on the visibility setting.
     *
     * @param visibilitySetting the setting type that determines visibility
     * @param friendUUID        the UUID of the friend to check visibility against
     * @return true if the player is visible to the friend, false otherwise
     */
    public boolean isVisibleTo(SettingType visibilitySetting, UUID friendUUID) {
        GlobalPolicy level = getSetting(visibilitySetting);

        return switch (level) {
            case EVERYONE -> true;
            case FRIENDS -> FriendManager.areFriends(friendUUID, playerUUID);
            case CITY_MEMBERS -> areSameCityMembers(playerUUID, friendUUID);
            case NOBODY -> false;
        };
    }

    /**
     * Checks if the player is visible to a friend based on the visibility setting.
     * This method checks if the player and the friend are friends before checking visibility.
     *
     * @param visibilitySetting the setting type that determines visibility
     * @param friendUUID        the UUID of the friend to check visibility against
     * @return true if the player is visible to the friend, false otherwise
     */
    public boolean isVisibleToFriend(SettingType visibilitySetting, UUID friendUUID) {
        if (!FriendManager.areFriends(playerUUID, friendUUID)) return false;
        return isVisibleTo(visibilitySetting, friendUUID);
    }

    /**
     * Checks if the player is visible to everyone based on the visibility setting.
     *
     * @param visibilitySetting the setting type that determines visibility
     * @return true if the player is visible to everyone, false otherwise
     */
    public boolean isVisibleToEveryone(SettingType visibilitySetting) {
        GlobalPolicy level = getSetting(visibilitySetting);
        return level == GlobalPolicy.EVERYONE;
    }

    /**
     * Checks if two players are members of the same city.
     *
     * @param player1UUID the UUID of the first player
     * @param player2UUID the UUID of the second player
     * @return true if both players are members of the same city, false otherwise
     */
    private boolean areSameCityMembers(UUID player1UUID, UUID player2UUID) {
        City player2City = CityManager.getPlayerCity(player2UUID);
        Player player1 = Bukkit.getPlayer(player1UUID);
        return player1 != null && player2City != null && player2City.isMember(player1);
    }
}