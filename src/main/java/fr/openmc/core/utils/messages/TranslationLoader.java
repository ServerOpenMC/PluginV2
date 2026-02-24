package fr.openmc.core.utils.messages;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationLoader {

    /**
     * Initialize the translation system with the given supported languages.
     * The translations should be in the "resources.translations" package, in a file named "lang_xx_XX.properties" where xx is the language code (ex. "fr_FR" for French).
     * @param langsSuppoorted the supported languages (ex. Locale.FRANCE, Locale.ENGLISH, etc.)
     */
    public static void init(Locale... langsSuppoorted) {
        MiniMessageTranslationStore store = MiniMessageTranslationStore.create(Key.key("openmc:translations"));

        for (Locale locale : langsSuppoorted) {
            ResourceBundle bundle = ResourceBundle.getBundle("translations.lang", locale, UTF8ResourceBundleControl.utf8ResourceBundleControl());
            store.registerAll(locale, bundle, true);
            OMCPlugin.getInstance().getSLF4JLogger().info("\u001B[32m✔ Chargement de la langue {} !\u001B[0m", locale.getDisplayName());
        }

        GlobalTranslator.translator().addSource(store);
    }
}
