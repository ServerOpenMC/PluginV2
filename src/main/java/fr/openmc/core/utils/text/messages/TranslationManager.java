package fr.openmc.core.utils.text.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.integration.ResourcePacksGenerator;
import fr.openmc.core.utils.text.ComponentUtils;
import fr.openmc.core.utils.types.MultiResourceBundle;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Gestionnaire des traductions du plugin pour plusieurs langues.
 * Gère le chargement, conversion et injection des traductions via resource pack.
 */
@SuppressWarnings("UnstableApiUsage")
public class TranslationManager {
    /** Traductions de fallback (langue par défaut du serveur - Français) */
    public static Map<String, String> fallbackTranslations = new HashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();

    /**
     * Initialise le gestionnaire de traductions et génère les ressource packs.
     * Charge les traductions en MiniMessage et les convertit en format legacy pour Minecraft.
     * 
     * @param context Le contexte de bootstrap du plugin Paper
     * @param defaultLang La langue par défaut du serveur (fallback)
     * @param langsSuppoorted Les langues additionnelles à supporter
     */
    public static void init(BootstrapContext context, Locale defaultLang, Locale... langsSuppoorted) {
        // * Generate resource pack
        Path resourcePackFolder;
        try {
            resourcePackFolder=ResourcePacksGenerator.generateBase(context, "generated-rp-langs");
            Files.createDirectories(resourcePackFolder.resolve("assets/minecraft/lang"));
            OMCLogger.successFormatted("Génération du resource pack de langues !");
        } catch (Exception e) {
            OMCLogger.errorFormatted("Erreur lors de la génération du resource pack de langues !", e);
            return;
        }

        // * Load default lang
        MultiResourceBundle defaultBundle = new MultiResourceBundle(
                "translations",
                defaultLang
        );

        fallbackTranslations = toLegacyMap(defaultBundle.getAllTranslations());
        try {
            injectLangs(resourcePackFolder, fallbackTranslations, defaultLang);
        } catch (Exception e) {
            OMCLogger.errorFormatted("Erreur lors de l'injection de la langue par défaut !", e);
            return;
        }
        OMCLogger.successFormatted("Chargement de la langue {} (par défaut) !", defaultLang.getDisplayName());

        // * Load other supported langs
        for (Locale locale : langsSuppoorted) {
            MultiResourceBundle bundle = new MultiResourceBundle("translations", locale);

            Map<String, String> translations = new HashMap<>(fallbackTranslations);
            Map<String, String> localeTranslations = toLegacyMap(bundle.getAllTranslations());

            translations.putAll(localeTranslations);


            try {
                injectLangs(resourcePackFolder, translations, locale);
            } catch (Exception e) {
                OMCLogger.errorFormatted("Erreur lors de l'injection des langues !", e);
                return;
            }

            OMCLogger.successFormatted("Chargement de la langue {} !", locale.getDisplayName());
        }
    }

    /**
     * Crée un composant texte traduisible avec arguments.
     * Le client Minecraft cherchera la traduction dans son resource pack.
     * 
     * @param key La clé de traduction (ex: "command.fun.playtime.success")
     * @param args Les arguments à interpoler dans la traduction
     * @return Un composant Paper Adventure traduisible (italique désactivé)
     */
    public static Component translation(String key, ComponentLike... args) {
        String fallback = getFallbackTranslation(key);
        ComponentLike[] normalizedArgs = ComponentUtils.normalizeComponent(args);

        return Component.translatable(
                key,
                fallback,
                normalizedArgs
        ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Retourne une traduction sous forme de String au format legacy.
     * 
     * @param key La clé de traduction
     * @param args Les arguments à interpoler
     * @return Une chaîne au format legacy (codes §)
     */
    public static String translationString(String key, ComponentLike... args) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(translation(key, args));
    }

    /**
     * Crée une liste de lignes traduisibles pour une lore d'objet.
     * Divise le texte en plusieurs lignes (séparées par \n).
     * 
     * @param key La clé de traduction
     * @param componentsArgs Les arguments de composants à interpoler
     * @return Une liste de composants, un par ligne (italique désactivé)
     */
    public static List<Component> translationLore(String key, ComponentLike... componentsArgs) {
        String fallback = fallbackTranslations.getOrDefault(key, key);

        ComponentLike[] normalizedArgs = ComponentUtils.normalizeComponent(componentsArgs);
        TranslatableComponent translatable = Component.translatable(key, normalizedArgs).fallback(fallback);

        String legacy = LegacyComponentSerializer.legacySection().serialize(translatable);

        String[] lines = legacy.split("\n");

        List<Component> lore = new ArrayList<>();

        for (String line : lines) {
            lore.add(LegacyComponentSerializer.legacySection().deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        return lore;
    }

    /**
     * Convertit une carte de traductions du format MiniMessage au format legacy.
     * 
     * @param miniMessageMap La carte de traductions en format MiniMessage
     * @return Une nouvelle carte avec les valeurs converties en format legacy (§)
     */
    private static Map<String, String> toLegacyMap(Map<String, String> miniMessageMap) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : miniMessageMap.entrySet()) {
            result.put(entry.getKey(), MessageConvertor.toLegacy(entry.getValue()));
        }
        return result;
    }

    /**
     * Récupère la traduction de secours (fallback) pour une clé.
     * 
     * @param key La clé de traduction
     * @return La traduction de fallback, ou la clé si elle n'existe pas
     */
    private static String getFallbackTranslation(String key) {
        return fallbackTranslations.getOrDefault(key, key);
    }

    /**
     * Génère le fichier JSON de traductions pour une locale donnée.
     * Crée un fichier dans assets/minecraft/lang/{locale}.json du resource pack.
     * 
     * @param resourcePackFolder Le chemin racine du resource pack
     * @param translations La carte des traductions à injecter
     * @param locale La locale cible (ex: fr_FR, en_US)
     * @throws IOException En cas d'erreur lors de l'écriture du fichier
     */
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
}
