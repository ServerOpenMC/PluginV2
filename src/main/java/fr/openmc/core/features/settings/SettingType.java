package fr.openmc.core.features.settings;

import fr.openmc.core.features.settings.policy.*;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum SettingType {

    // Friendship settings
    FRIEND_REQUESTS_POLICY(ValueType.ENUM, FriendPolicy.EVERYONE, "Politique des demandes d'amis",
            Material.PLAYER_HEAD, "Qui peut t'envoyer des demandes d'amis:"),

    // City settings
    CITY_JOIN_REQUESTS_POLICY(ValueType.ENUM, CityPolicy.EVERYONE, "Politique des demandes de rejoindre une ville",
            Material.PAPER, "Qui peut te demander à rejoindre une ville:"),

    // Mailbox settings
    MAILBOX_RECEIVE_POLICY(ValueType.ENUM, GlobalPolicy.EVERYONE, "Politique de réception des mailbox",
            Material.PAPER, "Qui peut t'envoyer des mailbox:"),

    // General settings
    PRIVATE_MESSAGE_POLICY(ValueType.ENUM, GlobalPolicy.EVERYONE, "Recevoir les messages privés",
            Material.WRITABLE_BOOK, "Qui peut t'envoyer des messages privés:"),
    NOTIFICATIONS_SOUND(ValueType.BOOLEAN, true, "Sons des messages",
            Material.NOTE_BLOCK, Material.GRAY_DYE, "Activer ou désactiver les sons des messages privés"),

    ;

    private final ValueType valueType;
    private final Object defaultValue;
    private final String name;
    private final Material enabledMaterial;
    private final Material disabledMaterial;
    private final String enumDescription;

    SettingType(ValueType valueType, Object defaultValue, String name,
                Material enabledMaterial, Material disabledMaterial, String enumDescription) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.name = name;
        this.enabledMaterial = enabledMaterial;
        this.disabledMaterial = disabledMaterial;
        this.enumDescription = enumDescription;
    }

    SettingType(ValueType valueType, Object defaultValue, String name, Material enabledMaterial,
                String enumDescription) {
        this(valueType, defaultValue, name, enabledMaterial, enabledMaterial, enumDescription);
    }


    public boolean isValidValue(Object value) {
        if (value == null) return false;

        return switch (valueType) {
            case BOOLEAN -> value instanceof Boolean;
            case INTEGER -> value instanceof Integer;
            case STRING -> value instanceof String;
            case ENUM ->  {
                if (defaultValue != null && defaultValue.getClass().isEnum()) {
                    yield defaultValue.getClass().isInstance(value);
                }
                yield false;
            }
        };
    }

    public Object parseValue(String value) {
        return switch (valueType) {
            case BOOLEAN -> Boolean.parseBoolean(value);
            case INTEGER -> Integer.parseInt(value);
            case STRING -> value;
            case ENUM -> {
                if (defaultValue != null && defaultValue.getClass().isEnum()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) defaultValue.getClass();
                    yield parseEnum(enumClass, value);
                }
                yield value;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> T parseEnum(Class<? extends Enum<?>> enumClass, String value) {
        return Enum.valueOf((Class<T>) enumClass, value);
    }
}