package fr.openmc.core.utils.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.openmc.core.utils.ResourcePacksGenerator;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    private static final ResourceBundle.Control UTF8_NO_FALLBACK_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return null; // suppression du fallback (en_US, afin d'eviter d'avoir des clés fr écraser)
        }

        @Override
        public List<String> getFormats(String baseName) {
            return ResourceBundle.Control.FORMAT_PROPERTIES;
        }

        @Override
        public ResourceBundle newBundle(
                String baseName,
                Locale locale,
                String format,
                ClassLoader loader,
                boolean reload
        ) throws IOException {
            if (!"java.properties".equals(format)) {
                return null;
            }

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");

            InputStream stream = null;
            if (reload) {
                var url = loader.getResource(resourceName);
                if (url != null) {
                    var connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }

            if (stream == null) {
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return new PropertyResourceBundle(reader);
            }
        }
    };

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
                UTF8_NO_FALLBACK_CONTROL
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
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "translations.lang",
                    locale,
                    UTF8_NO_FALLBACK_CONTROL
            );

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
        ComponentLike[] normalizedArgs = normalizeComponent(args);

        return Component.translatable(
                key,
                fallback,
                normalizedArgs
        ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String translationString(String key, ComponentLike... args) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(translation(key, args));
    }

    public static List<Component> translationLore(String key, ComponentLike... componentsArgs) {
        String fallback = fallbackTranslations.getOrDefault(key, key);

        ComponentLike[] normalizedArgs = normalizeComponent(componentsArgs);
        TranslatableComponent translatable = Component.translatable(key, normalizedArgs).fallback(fallback);

        String legacy = LegacyComponentSerializer.legacySection().serialize(translatable);

        String[] lines = legacy.split("\n");

        List<Component> lore = new ArrayList<>();

        for (String line : lines) {
            lore.add(LegacyComponentSerializer.legacySection().deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        return lore;
    }

    private static ComponentLike[] normalizeComponent(ComponentLike... args) {
        if (args == null || args.length == 0) return new ComponentLike[0];

        ComponentLike[] normalized = new ComponentLike[args.length];
        for (int i = 0; i < args.length; i++) {
            ComponentLike like = args[i];
            if (like == null) {
                normalized[i] = Component.empty();
                continue;
            }
            Component component = like.asComponent();
            normalized[i] = component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE);
        }
        return normalized;
    }
}
