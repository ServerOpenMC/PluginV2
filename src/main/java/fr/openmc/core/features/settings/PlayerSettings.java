package fr.openmc.core.features.settings;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.settings.policy.CityJoinPolicy;
import fr.openmc.core.features.settings.policy.FriendRequestPolicy;
import fr.openmc.core.features.settings.policy.VisibilityLevel;
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

    private Object parseValue(SettingType settingType, String value) {
        return switch (settingType.getValueType()) {
            case BOOLEAN -> Boolean.parseBoolean(value);
            case INTEGER -> Integer.parseInt(value);
            case STRING -> value;
            case ENUM -> {
                if (settingType == SettingType.FRIEND_REQUESTS_POLICY) {
                    yield FriendRequestPolicy.valueOf(value);
                } else if (settingType == SettingType.CITY_JOIN_REQUESTS_POLICY) {
                    yield CityJoinPolicy.valueOf(value);
                } else if (settingType == SettingType.FRIEND_VISIBILITY_MONEY ||
                        settingType == SettingType.FRIEND_VISIBILITY_CITY) {
                    yield VisibilityLevel.valueOf(value);
                }
                yield value;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(SettingType settingType) {
        if (!loaded) {
            loadDefaultSettings();
            return (T) settingType.getDefaultValue();
        }
        System.out.println("Getting setting " + settingType + " for player " + playerUUID);
        System.out.println("Current settings: " + settings);
        return (T) settings.getOrDefault(settingType, settingType.getDefaultValue());
    }

    public void setSetting(SettingType settingType, Object value) {
        try {
            if (!settingType.isValidValue(value)) {
                throw new IllegalArgumentException("Invalid value for setting: " + settingType);
            }
            settings.put(settingType, value);
            System.out.println("Setting " + settingType + " for player " + playerUUID + " to " + value);
            System.out.println("Current settings: " + settings);
        } catch (Exception e) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§cErreur: " + e.getMessage());
            }
            e.printStackTrace();
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

    public boolean canReceiveFriendRequest(UUID senderUUID) {
        FriendRequestPolicy policy = getSetting(SettingType.FRIEND_REQUESTS_POLICY);

        return switch (policy) {
            case EVERYONE -> true;
            case CITY_MEMBERS_ONLY -> {
                yield areSameCityMembers(playerUUID, senderUUID);
            }
            case NOBODY -> false;
        };
    }

    public boolean canReceiveCityJoinRequest(UUID senderUUID) {
        CityJoinPolicy policy = getSetting(SettingType.CITY_JOIN_REQUESTS_POLICY);

        return switch (policy) {
            case EVERYONE -> true;
            case FRIENDS_ONLY -> {
                yield areFriends(playerUUID, senderUUID);
            }
            case NOBODY -> false;
        };
    }

    public boolean isVisibleToFriend(SettingType visibilitySetting, UUID friendUUID) {
        if (!areFriends(playerUUID, friendUUID)) {
            return false;
        }

        VisibilityLevel level = getSetting(visibilitySetting);
        return level == VisibilityLevel.FRIENDS || level == VisibilityLevel.EVERYONE;
    }

    public boolean isVisibleToEveryone(SettingType visibilitySetting) {
        VisibilityLevel level = getSetting(visibilitySetting);
        return level == VisibilityLevel.EVERYONE;
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
