package fr.openmc.core.utils;

import java.util.Arrays;
import java.util.List;
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

    /**
     * Récupère toutes les valeurs de l'enum situées avant la valeur indiquée.
     *
     * @param value la valeur de référence de l'enum
     * @param <T> le type de l'enum
     * @return une liste contenant les valeurs précédant la valeur indiquée
     */
    public static <T extends Enum<T>> List<T> getValuesBefore(T value, boolean withIt) {
        return Arrays.stream(value.getDeclaringClass().getEnumConstants())
                .filter(e -> withIt ? e.ordinal() <= value.ordinal() : e.ordinal() < value.ordinal())
                .toList();
    }

    /**
     * Récupère toutes les valeurs de l'enum situées après la valeur indiquée.
     *
     * @param value la valeur de référence de l'enum
     * @param <T> le type de l'enum
     * @return une liste contenant les valeurs suivant la valeur indiquée
     */
    public static <T extends Enum<T>> List<T> getValuesAfter(T value, boolean withIt) {
        return Arrays.stream(value.getDeclaringClass().getEnumConstants())
                .filter(e -> withIt ? e.ordinal() >= value.ordinal() : e.ordinal() > value.ordinal())
                .toList();
    }

    /**
     * Récupère la valeur de l'enum située après la valeur indiquée.
     *
     * @param value la valeur de référence de l'enum
     * @param <T> le type de l'enum
     * @return la valeur suivante, ou null si elle n'existe pas
     */
    public static <T extends Enum<T>> T getValueAfter(T value) {
        T[] values = value.getDeclaringClass().getEnumConstants();
        int index = value.ordinal() + 1;

        return index < values.length ? values[index] : null;
    }

    /**
     * Récupère la valeur de l'enum située avant la valeur indiquée.
     *
     * @param value la valeur de référence de l'enum
     * @param <T> le type de l'enum
     * @return la valeur précédente, ou null si elle n'existe pas
     */
    public static <T extends Enum<T>> T getValueBefore(T value) {
        T[] values = value.getDeclaringClass().getEnumConstants();
        int index = value.ordinal() - 1;

        return index >= 0 ? values[index] : null;
    }
}
