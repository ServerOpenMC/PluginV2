package fr.openmc.core.utils.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.openmc.core.utils.ResourcePacksGenerator;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class TranslationManager {
    public static Map<String, String> fallbackTranslations = new HashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();

    public static void init(BootstrapContext context, Locale defaultLang, Locale... langsSuppoorted) {
        // * Generate resource pack
        Path resourcePackFolder;
        try {
            resourcePackFolder=ResourcePacksGenerator.generateBase(context, "generated-rp-langs");
            Files.createDirectories(resourcePackFolder.resolve("assets/minecraft/lang"));
            context.getLogger().info("\u001B[32m✔ Génération du resource pack de langues !\u001B[0m");
        } catch (Exception e) {
            context.getLogger().error("\u001B[31m✘ Erreur lors de la génération du resource pack de langues !\u001B[0m", e);
            return;
        }

        // * Load default lang
        ResourceBundle defaultBundle = ResourceBundle.getBundle(
                "translations.lang",
                defaultLang,
                UTF8ResourceBundleControl.utf8ResourceBundleControl()
        );

        fallbackTranslations = new HashMap<>();
        for (String key : defaultBundle.keySet()) {
            fallbackTranslations.put(key, MessageConvertor.toLegacy(defaultBundle.getString(key)));
        }
        try {
            injectLangs(resourcePackFolder, fallbackTranslations, defaultLang);
        } catch (Exception e) {
            context.getLogger().error("\u001B[31m✘ Erreur lors de l'injection de la langue par défaut !\u001B[0m", e);
            return;
        }
        context.getLogger().info("\u001B[32m✔ Chargement de la langue {} (par défaut) !\u001B[0m", defaultLang.getDisplayName());

        // * Load other supported langs
        for (Locale locale : langsSuppoorted) {
            ResourceBundle bundle = ResourceBundle.getBundle("translations.lang", locale, UTF8ResourceBundleControl.utf8ResourceBundleControl());

            Map<String, String> translations = new HashMap<>(fallbackTranslations);

            for (String key : bundle.keySet()) {
                translations.put(key, MessageConvertor.toLegacy(bundle.getString(key)));
            }

            try {
                injectLangs(resourcePackFolder, translations, locale);
            } catch (Exception e) {
                context.getLogger().error("\u001B[31m✘ Erreur lors de l'injection des langues !\u001B[0m", e);
                return;
            }

            context.getLogger().info("\u001B[32m✔ Chargement de la langue {} !\u001B[0m", locale.getDisplayName());
        }
    }

    private static void injectLangs(Path resourcePackFolder, Map<String, String> translations, Locale locale) throws IOException {
        Path langFolder = resourcePackFolder.resolve("assets/minecraft/lang");

        String minecraftLocale = locale.toString().toLowerCase(); // fr_fr, en_us

        JsonObject root = new JsonObject();

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            root.addProperty(entry.getKey(), entry.getValue());
        }

        Files.writeString(
                langFolder.resolve(minecraftLocale + ".json"),
                GSON.toJson(root)
        );
    }

    private static String getFallbackTranslation(String key) {
        return fallbackTranslations.getOrDefault(key, key);
    }

    public static Component translation(String key, ComponentLike... args) {
        String fallback = getFallbackTranslation(key);

        return Component.translatable(
                key,
                fallback,
                args
        );
    }

    public static String translationString(String key, ComponentLike... args) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(translation(key, args));
    }

    public static List<Component> translationLore(String key, ComponentLike... componentsArgs) {
        String fallback = fallbackTranslations.getOrDefault(key, key);

        TranslatableComponent translatable = Component.translatable(key, componentsArgs).fallback(fallback);

        String legacy = LegacyComponentSerializer.legacySection().serialize(translatable);

        String[] lines = legacy.split("\n");

        List<Component> lore = new ArrayList<>();

        for (String line : lines) {
            lore.add(LegacyComponentSerializer.legacySection().deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        return lore;
    }
}
