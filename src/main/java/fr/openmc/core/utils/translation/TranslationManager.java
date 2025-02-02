package fr.openmc.core.utils.translation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.openmc.core.OMCPlugin;


public class TranslationManager {

    private final String defaultLanguage;
    private final OMCPlugin plugin;
    private final File translationFolder;
    private final Map<String, FileConfiguration> loadedLanguages;

    public TranslationManager(OMCPlugin plugin, File translationFolder, String defaultLanguage) {
        this.plugin = plugin;
        this.defaultLanguage = defaultLanguage;
        this.translationFolder = translationFolder;
        this.loadedLanguages = new HashMap<>();
    }

    /**
     * Returns a string corresponding to the specified path and the language.
     *
     * @param path  The path to the translation, "default" for default language"
     * @param language The language of the translation
     */
    public String getTranslation(String path, String language) {
        FileConfiguration languageConfig = this.loadedLanguages.get(language);
                this.loadedLanguages.get(this.defaultLanguage);
        if (languageConfig != null || Objects.equals(language, this.defaultLanguage)) {
            return languageConfig.getString(path, "Missing translation for path: " + path);
        } else {
            return getTranslation(path);
        }
    }

    /**
     * Returns a string corresponding to the specified path and the language and replaces the given placeholders with the values.
     *
     * @param path  The path to the translation
     * @param language The language of the translation, "default" for default language"
     * @param placeholders The placeholders you want to replace in pair with values ("player", player.getName())
     */
    public String getTranslation(String path, String language, String... placeholders) {
        return this.replacePlaceholders(getTranslation(path, language), placeholders);
    }

    /**
     * Returns a string corresponding to the specified path and the default language.
     *
     * @param path  The path to the translation
     */
    public String getTranslation(String path) {
        return this.getTranslation(path, this.defaultLanguage);
    }


    /**
     * Loads the specified language if it is present in the translations' folder.
     *
     * @param language The language to load
     */
    public void loadLanguage(String language) {

        File languageFile = new File(this.translationFolder, language + ".yml");

        if (!languageFile.exists()) {
            try {
                this.plugin.saveResource(translationFolder.getPath() + language + ".yml", false);
                plugin.getLogger().info("Language loaded : " + language);
            }
            catch (Exception ignored) {
                plugin.getLogger().warning("Language " + language + " does not exist");
            }
        }

        if (languageFile.exists()) {
            this.loadedLanguages.put(language, YamlConfiguration.loadConfiguration(languageFile));
            plugin.getLogger().info("Language " + language + " loaded");
        }
    }

    /**
     * Replaces the keys (between {}) in a String with the value Strings.
     *
     * @param text The string to modify
     * @param placeholders The placeholders you want to replace in pair with values ("player", player.getName())
     */
    public String replacePlaceholders(String text, String... placeholders) {
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
    public void loadAllLanguages() {
        if (!this.translationFolder.exists()) {
            this.translationFolder.mkdirs();

            // List of default languages
            String[] defaultLanguages = {"fr"};

            for (String lang : defaultLanguages) {
                String resourcePath = "translations/" + lang + ".yml";
                File targetFile = new File(this.translationFolder, lang + ".yml");

                if (!targetFile.exists()) {
                    this.plugin.saveResource(resourcePath, false);
                }
            }
        }

        File[] files = this.translationFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files!=null) {
            for (File file: files)  {
                this.loadLanguage(file.getName().replace(".yml", ""));
            }
        }

    }
}
