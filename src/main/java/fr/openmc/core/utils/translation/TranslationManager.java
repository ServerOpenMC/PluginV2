package fr.openmc.core.utils.translation;

import fr.openmc.core.OMCPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TranslationManager {

    private static String defaultLanguage;
    private static File translationFolder;
    private static final Map<String, FileConfiguration> loadedLanguages = new HashMap<>();

    public TranslationManager(File inTranslationFolder, String inDefaultLanguage) {
        defaultLanguage = inDefaultLanguage;
        translationFolder = inTranslationFolder;

        loadAllLanguages();
    }

    /**
     * Returns a string corresponding to the specified path and the language.
     *
     * @param path  The path to the translation, "default" for default language
     * @param language The language of the translation
     */
    public static String getTranslation(String path, String language) {
        FileConfiguration languageConfig = loadedLanguages.get(language);
                loadedLanguages.get(defaultLanguage);
        if (languageConfig != null || Objects.equals(language, defaultLanguage)) {
            return languageConfig.getString(path, "Missing translation for path: " + path);
        } else {
            return getTranslation(path);
        }
    }

    /**
     * Returns a string corresponding to the specified path and the language and replaces the given placeholders with the values.
     *
     * @param path  The path to the translation
     * @param language The language of the translation, "default" for default language
     * @param placeholders The placeholders you want to replace in pair with values ("player", player.getName())
     */
    public static String getTranslation(String path, String language, String... placeholders) {
        return replacePlaceholders(getTranslation(path, language), placeholders);
    }

    /**
     * Returns a string corresponding to the specified path and the default language.
     *
     * @param path  The path to the translation
     */
    public static String getTranslation(String path) {
        return getTranslation(path, defaultLanguage);
    }


    /**
     * Loads the specified language if it is present in the translations' folder.
     *
     * @param language The language to load
     */
    public static void loadLanguage(String language) {

        File languageFile = new File(translationFolder, language + ".yml");

        if (!languageFile.exists()) {
            try {
                OMCPlugin.getInstance().saveResource(translationFolder.getPath() + language + ".yml", false);
                OMCPlugin.getInstance().getSLF4JLogger().info("Language {} not found, creating a new one from default template.", language);
            }
            catch (Exception ignored) {
                OMCPlugin.getInstance().getSLF4JLogger().error("Failed to load the default language file: {}.yml. Please ensure it exists in the translations folder in the jar file.", language);
                return;
            }
        }

        if (languageFile.exists()) {
            loadedLanguages.put(language, YamlConfiguration.loadConfiguration(languageFile));
            OMCPlugin.getInstance().getSLF4JLogger().info("Language {} loaded successfully.", language);
        }
    }

    /**
     * Replaces the keys (between {}) in a String with the value Strings.
     *
     * @param text The string to modify
     * @param placeholders The placeholders you want to replace in pair with values ("player", player.getName())
     */
    public static String replacePlaceholders(String text, String... placeholders) {
        for (int i = 0; i < placeholders.length; i += 2) {
            String key = placeholders[i];
            String value = placeholders[i + 1];
            text = text.replace("{" + key + "}", value);
        }
        return text;
    }

    /**
     * Loads all the languages present in the translations folder.
     */
    public static void loadAllLanguages() {
        if (!translationFolder.exists()) {
            translationFolder.mkdirs();

            // List of default languages
            String[] defaultLanguages = {"fr"};

            for (String lang : defaultLanguages) {
                String resourcePath = "translations/" + lang + ".yml";
                File targetFile = new File(translationFolder, lang + ".yml");

                if (!targetFile.exists()) {
                    OMCPlugin.getInstance().saveResource(resourcePath, false);
                }
            }
        }

        File[] files = translationFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files!=null) {
            for (File file: files)  {
                loadLanguage(file.getName().replace(".yml", ""));
            }
        }

    }
}
