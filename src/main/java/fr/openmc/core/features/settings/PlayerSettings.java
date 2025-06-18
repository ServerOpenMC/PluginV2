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

    public PlayerSettings(UUID playerUUID) {
        this.playerUUID = playerUUID;
        loadDefaultSettings();
    }

    private void loadDefaultSettings() {
        for (SettingType settingType : SettingType.values()) {
            settings.put(settingType, settingType.getDefaultValue());
        }
        loaded = true;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(SettingType settingType) {
        if (!loaded) {
            loadDefaultSettings();
            return (T) settingType.getDefaultValue();
        }
        return (T) settings.getOrDefault(settingType, settingType.getDefaultValue());
    }

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

    public void resetSetting(SettingType settingType) {
        setSetting(settingType, settingType.getDefaultValue());
    }

    public void resetAllSettings() {
        for (SettingType settingType : SettingType.values()) {
            resetSetting(settingType);
        }
    }

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
                case FRIENDS -> areFriends(playerUUID, targetUUID);
                case NOBODY -> false;
            };
        }

        if (policy instanceof GlobalPolicy globalPolicy) {
            return switch (globalPolicy) {
                case EVERYONE -> true;
                case FRIENDS -> areFriends(playerUUID, targetUUID);
                case CITY_MEMBERS -> areSameCityMembers(playerUUID, targetUUID);
                case NOBODY -> false;
            };
        }

        throw new IllegalArgumentException("Unsupported policy: " + policy);
    }

    public boolean canReceiveFriendRequest(UUID senderUUID) {
        return canPerformAction(SettingType.FRIEND_REQUESTS_POLICY, senderUUID);
    }

    public boolean canReceiveCityJoinRequest(UUID senderUUID) {
        return canPerformAction(SettingType.CITY_JOIN_REQUESTS_POLICY, senderUUID);
    }

    public boolean canReceivePrivateMessage(UUID senderUUID) {
        return canPerformAction(SettingType.PRIVATE_MESSAGE_POLICY, senderUUID);
    }

    public boolean isVisibleTo(SettingType visibilitySetting, UUID friendUUID) {
        GlobalPolicy level = getSetting(visibilitySetting);

        return switch (level) {
            case EVERYONE -> true;
            case FRIENDS -> areFriends(friendUUID, playerUUID);
            case CITY_MEMBERS -> areSameCityMembers(playerUUID, friendUUID);
            case NOBODY -> false;
        };
    }

    public boolean isVisibleToFriend(SettingType visibilitySetting, UUID friendUUID) {
        if (!areFriends(playerUUID, friendUUID)) return false;
        return isVisibleTo(visibilitySetting, friendUUID);
    }

    public boolean isVisibleToEveryone(SettingType visibilitySetting) {
        GlobalPolicy level = getSetting(visibilitySetting);
        return level == GlobalPolicy.EVERYONE;
    }

    private boolean areSameCityMembers(UUID player1UUID, UUID player2UUID) {
        City player2City = CityManager.getPlayerCity(player2UUID);
        Player player1 = Bukkit.getPlayer(player1UUID);
        if (player1 != null && player2City != null)
            return player2City.isMember(player1);
        return false;
    }

    private boolean areFriends(UUID player1, UUID player2) {
        return FriendManager.getInstance().areFriends(player1, player2);
    }
}