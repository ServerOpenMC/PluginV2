package fr.openmc.core.hooks.itemsadder.placeholders;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.dream.placeholders.DreamItemMaterialPlaceholder;
import fr.openmc.core.features.dream.placeholders.DreamItemNamePlaceholder;
import fr.openmc.core.features.dream.placeholders.DreamItemTooltipPlaceholder;

import java.util.HashMap;
import java.util.Map;

/**
 * Registre des placeholders ItemsAdder et moteur de remplacement.
 */
public class IAPlaceholderRegistry {
    private static final String PLACEHOLDER_FORMAT = "{%s:%s}";

    private static final Map<String, IAPlaceholder> placeholders = new HashMap<>();

    /**
     * @return un registre préchargé avec les placeholders par défaut.
     */
    public static IAPlaceholderRegistry loadDefault() {
        IAPlaceholderRegistry registry = new IAPlaceholderRegistry();

        registry.register(
                new DreamItemTooltipPlaceholder(),
                new DreamItemMaterialPlaceholder(),
                new DreamItemNamePlaceholder()
        );
        return registry;
    }

    /**
     * Enregistre les placeholders
     * @param placeholders placeholders à enregistrer.
     */
    public void register(IAPlaceholder... placeholders) {
        for (IAPlaceholder placeholder : placeholders) {
            register(placeholder);
        }
    }

    /**
     * Enregistre un placeholder
     * @param placeholder placeholder à enregistrer.
     */
    public void register(IAPlaceholder placeholder) {
        if (placeholder == null) {
            return;
        }
        placeholders.put(placeholder.name(), placeholder);
    }

    /**
     * Remplace les placeholders dans une chaîne.
     *
     * @param input chaîne d'entrée.
     * @return chaîne avec placeholders remplacés.
     */
    public String applyPlaceholders(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder output = new StringBuilder(input.length());
        int cursor = 0;

        // * Teste les comibnaisons de placeholders dans la chaîne d'entrée
        // * renvoie le résultat dans output
        while (cursor < input.length()) {
            int openIndex = input.indexOf('{', cursor);
            if (openIndex < 0) {
                output.append(input, cursor, input.length());
                break;
            }

            output.append(input, cursor, openIndex);
            int closeIndex = input.indexOf('}', openIndex + 1);
            if (closeIndex < 0) {
                output.append(input, openIndex, input.length());
                break;
            }

            int colonIndex = input.indexOf(':', openIndex + 1);
            if (colonIndex < 0 || colonIndex > closeIndex) {
                output.append(input, openIndex, closeIndex + 1);
                cursor = closeIndex + 1;
                continue;
            }

            String placeholderName = input.substring(openIndex + 1, colonIndex);
            String argument = input.substring(colonIndex + 1, closeIndex);
            IAPlaceholder placeholder = placeholders.get(placeholderName);

            if (placeholder == null) {
                output.append(input, openIndex, closeIndex + 1);
                cursor = closeIndex + 1;
                continue;
            }

            String resolved = placeholder.resolve(argument);
            if (resolved == null) {
                String key = placeholderName + ":" + argument;
                OMCLogger.warnFormatted("Placeholder ItemsAdder introuvable: {}", key);

                output.append(formatPlaceholder(placeholderName, argument));
                cursor = closeIndex + 1;
                continue;
            }

            output.append(resolved);
            cursor = closeIndex + 1;
        }

        return output.toString();
    }

    /**
     * Mets le placeholer au format {name:argument}
     * @param name nom du placeholder.
     * @param argument argument brut.
     * @return placeholder reconstitué au format "{name:arg}".
     */
    private static String formatPlaceholder(String name, String argument) {
        return String.format(PLACEHOLDER_FORMAT, name, argument);
    }

    /**
     * @return nombre de placeholders enregistrés.
     */
    public int getPlaceholdersCount() {
        return placeholders.size();
    }
}
