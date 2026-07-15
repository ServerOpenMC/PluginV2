package fr.openmc.core.features.settings;

import fr.openmc.core.features.settings.policy.CityPolicy;
import fr.openmc.core.features.settings.policy.FriendPolicy;
import fr.openmc.core.features.settings.policy.GlobalPolicy;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum SettingType {

    // - Friendship settings
    FRIEND_REQUESTS_POLICY(ValueType.ENUM, FriendPolicy.EVERYONE, "feature.settings.type.friend_requests.name",
            Material.PLAYER_HEAD, "feature.settings.type.friend_requests.description"),

    // - City settings
    CITY_JOIN_REQUESTS_POLICY(ValueType.ENUM, CityPolicy.EVERYONE, "feature.settings.type.city_join_requests.name",
            Material.PAPER, "feature.settings.type.city_join_requests.description"),
    MASCOT_PLAY_SOUND_POLICY(ValueType.BOOLEAN, true, "feature.settings.type.mascot_play_sound.name",
            Material.MUSIC_DISC_LAVA_CHICKEN, Material.GRAY_DYE, "feature.settings.type.mascot_play_sound.description", DataComponentTypes.JUKEBOX_PLAYABLE),

    // - Mailbox settings
    MAILBOX_RECEIVE_POLICY(ValueType.ENUM, GlobalPolicy.EVERYONE, "feature.settings.type.mailbox_receive.name",
            Material.PAPER, "feature.settings.type.mailbox_receive.description"),

    // - General settings
    PRIVATE_MESSAGE_POLICY(ValueType.ENUM, GlobalPolicy.EVERYONE, "feature.settings.type.private_message.name",
            Material.WRITABLE_BOOK, "feature.settings.type.private_message.description"),
    NOTIFICATIONS_SOUND(ValueType.BOOLEAN, true, "feature.settings.type.notifications_sound.name",
            Material.NOTE_BLOCK, Material.GRAY_DYE, "feature.settings.type.notifications_sound.description"),
    TELEPORT_TITLE_FADE(ValueType.BOOLEAN, true, "feature.settings.type.teleport_title_fade.name",
            Material.ENDER_PEARL, Material.GRAY_DYE, "feature.settings.type.teleport_title_fade.description"),
    JOIN_ANIMATION(ValueType.BOOLEAN, true, "feature.settings.type.join_animation.name",
            Material.GLOW_INK_SAC, Material.INK_SAC, "feature.settings.type.join_animation.description"),

    ;

    private final ValueType valueType;
    private final Object defaultValue;
    private final String name;
    private final Material enabledMaterial;
    private final Material disabledMaterial;
    private final String enumDescription;
    private final DataComponentType[] dataComponentType;

    /**
     * Enum representing the type of setting, its default value, name, materials for enabled/disabled states,
     * and a description for enum values.
     *
     * @param valueType          The type of value this setting holds.
     * @param defaultValue       The default value for this setting.
     * @param name               The name of the setting.
     * @param enabledMaterial    Material representing the enabled state.
     * @param disabledMaterial   Material representing the disabled state.
     * @param enumDescription    Description for enum values.
     */
    SettingType(ValueType valueType, Object defaultValue, String name,
                Material enabledMaterial, Material disabledMaterial, String enumDescription, DataComponentType... dataToHide) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.name = name;
        this.enabledMaterial = enabledMaterial;
        this.disabledMaterial = disabledMaterial;
        this.enumDescription = enumDescription;
        this.dataComponentType = dataToHide;
    }

    /**
     * Constructor for SettingType without a disabled material.
     *
     * @param valueType          The type of value this setting holds.
     * @param defaultValue       The default value for this setting.
     * @param name               The name of the setting.
     * @param enabledMaterial    Material representing the enabled state.
     * @param enumDescription    Description for enum values.
     */
    SettingType(ValueType valueType, Object defaultValue, String name, Material enabledMaterial,
                String enumDescription, DataComponentType... dataToHide) {
        this(valueType, defaultValue, name, enabledMaterial, enabledMaterial, enumDescription, dataToHide);
    }



    /**
     * Checks if the provided value is valid for this setting type.
     *
     * @param value the value to check
     * @return true if the value is valid, false otherwise
     */
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

    /**
     * Parses a string value into the appropriate type based on the setting's value type.
     *
     * @param value the string value to parse
     * @return the parsed value as an Object
     */
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

    /**
     * Parses a string value into an enum of the specified class.
     *
     * @param enumClass the class of the enum to parse
     * @param value     the string value to parse
     * @param <T>       the type of the enum
     * @return the parsed enum value
     */
    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> T parseEnum(Class<? extends Enum<?>> enumClass, String value) {
        return Enum.valueOf((Class<T>) enumClass, value);
    }
}
