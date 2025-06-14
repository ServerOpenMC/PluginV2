package fr.openmc.core.features.settings;

import fr.openmc.core.features.settings.policy.CityJoinPolicy;
import fr.openmc.core.features.settings.policy.FriendRequestPolicy;
import lombok.Getter;

@Getter
public enum SettingType {

    // Friendship settings
    FRIEND_REQUESTS_ENABLED(ValueType.BOOLEAN, true, "Activer les demandes d'amis"),
    FRIEND_REQUESTS_POLICY(ValueType.ENUM, FriendRequestPolicy.EVERYONE, "Politique des demandes d'amis"),
    FRIEND_CONNECTION_MESSAGES(ValueType.BOOLEAN, true, "Messages de connexion/déconnexion des amis"),

    // City settings
    CITY_JOIN_REQUESTS_POLICY(ValueType.ENUM, CityJoinPolicy.EVERYONE, "Politique des demandes de rejoindre une ville"),

    // General settings
    PRIVATE_MESSAGES_ENABLED(ValueType.BOOLEAN, true, "Recevoir les messages privés"),
    NOTIFICATIONS_SOUND(ValueType.BOOLEAN, true, "Sons des messages"),

    ;

    private final ValueType valueType;
    private final Object defaultValue;
    private final String description;

    SettingType(ValueType valueType, Object defaultValue, String description) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public boolean isValidValue(Object value) {
        if (value == null) return false;

        return switch (valueType) {
            case BOOLEAN -> value instanceof Boolean;
            case INTEGER -> value instanceof Integer;
            case STRING -> value instanceof String;
            case ENUM -> {
                if (this == FRIEND_REQUESTS_POLICY)
                    yield value instanceof FriendRequestPolicy;
                else if (this == CITY_JOIN_REQUESTS_POLICY)
                    yield value instanceof CityJoinPolicy;
                else
                    yield false;
            }
        };
    }

}