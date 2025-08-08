package fr.openmc.core.utils;

import java.util.Locale;

public class EnumUtils {
    /**
     * Matches a string key to an enum constant.
     *
     * <p>This is a convenience method that returns <code>null</code> if no matching constant is found.</p>
     *
     * @param key the string key to match
     * @param enumClass the enum class to search in
     * @param <T> the enum type
     * @return the matched enum constant, or <code>null</code> if no match found
     */
    public static <T extends Enum<T>> T match(String key, Class<T> enumClass) {
        return match(key, enumClass, null);
    }

    /**
     * Safely matches a string key to an enum constant.
     *
     * @param key The string key to match
     * @param enumClass The enum class to search in
     * @param <T> The enum type
     * @return An Optional containing the matched enum constant, or empty if no match found
     */
    public static <T extends Enum<T>> T match(String key, Class<T> enumClass, T defaultValue) {
        try {
            return Enum.valueOf(enumClass, key.toUpperCase(Locale.ROOT));
        } catch (NullPointerException | IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
